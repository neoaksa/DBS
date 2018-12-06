package edu.gvsu.restapi.client;

/**
 * <p>Title: Lab2</p>
 * <p>Description: Old School Instant Messaging Application </p>
 * @author Jonathan Engelsma
 * @version 1.0
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import java.net.*;
import java.io.*;


/**
 * A simple chat client.
 */
public class ChatClient
{

//    PresenceService nameServer;
    ServerSocket serviceSkt = null;
    SvrThread svrThread;
    RegistrationInfo regInfo;
    public static final String APPLICATION_URI = "http://reflecting-poet-203512.appspot.com";
    String widgetsResourceURL = null;
    Request request = null;
    Response resp = null;

    /**
     * Constructor.
     * @param uname The name of the chimp running the client.
     * @parm hostPortStr The host/port string in the form host:port
     * where the ":port" portion is optional. This is the host/port
     * of the presence service we are connecting to.  If set to null,
     * we'll attempt to connect to port 1099 on the localhost.
     */
    public ChatClient(String uname,String hostPortStr)
    {
        // Step 0. Figure out local host name.
        String myHost;
        try {
            myHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            myHost = "localhost";
        }

        // Step 1. We need to establish a server socket where we will listen for
        // incoming chat requests.
        try {
            this.serviceSkt = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("Error: Couldn't allocate local socket endpoint.");
            System.exit(-1);
        }


        // Step 2. we bind to the nameserver so we can register our client.
        if(hostPortStr == null) {
            hostPortStr = myHost;
        }
//        System.out.println("User name is " + uname);
//
//        System.out.println("Trying to connect to name server at " + hostPortStr);

        // Step 3. Create the registration info bundle.
        // check if userName has been take
        widgetsResourceURL = APPLICATION_URI + "/users/"+ uname;//?????
        request = new Request(Method.GET,widgetsResourceURL);

        // We need to ask specifically for JSON
        request.getClientInfo().getAcceptedMediaTypes().
                add(new Preference(MediaType.APPLICATION_JSON));

        // Now we do the HTTP GET
        resp = new Client(Protocol.HTTP).handle(request);

        // Let's see what we got!
        if(!resp.getStatus().equals(Status.SUCCESS_OK)) {

            this.regInfo = new RegistrationInfo(uname, myHost, this.serviceSkt.getLocalPort(), true);

            // Step 4. register the client with the presence service to advertise it
            // is available for chatting.
            Form form = new Form();
            form.add("name", uname);
            form.add("port", Integer.toString(this.serviceSkt.getLocalPort()));
//            form.add("status", String.valueOf(true)); //set true as default
            form.add("host", myHost);

            // construct request to create a new widget resource
            widgetsResourceURL = APPLICATION_URI + "/users";
            request = new Request(Method.POST, widgetsResourceURL);

            // set the body of the HTTP POST command with form data.
            request.setEntity(form.getWebRepresentation());

            // Invoke the client HTTP connector to send the POST request to the server.
            System.out.println("Sending an HTTP POST to " + widgetsResourceURL + ".");
            resp = new Client(Protocol.HTTP).handle(request);

            // now, let's check what we got in response.
//            System.out.println(resp.getStatus());
            Representation responseData = resp.getEntity();
            try {
                System.out.println(responseData.getText());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else
        {
            System.out.println("Sorry, that username is already taken.  Please try another.");
            System.exit(1);
        }

        // Step 5. Kick off a separate thread to listen to incoming requests on the
        // Server socket.
        this.svrThread = new SvrThread();
        Thread t = new Thread(this.svrThread);
        t.start();
    }



    /**
     * Simple command shell that interprets commands from the user.
     */
    public void runCmdShell()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean done = false;
        String cmd;


        // Read and process commands from standard input, until done.
        while(!done) {

            this.promptUser();

            try {

                // Read an input line.
                cmd = in.readLine();

                // If we have a valid command, try to parse/process it.
                if (cmd != null) {

                    if (cmd.toLowerCase().trim().startsWith("chat")) {
                        // look up user in the presence server.
                        String str = cmd.toLowerCase().trim();
                        int pos = str.indexOf(' ');
                        if (pos == -1) {
                            System.out.println("Missing userName.  Enter: chat {username} {msg}");
                            continue;
                        }
                        str = str.substring(pos + 1);
                        pos = str.indexOf(' ');
                        String name;
                        String msg;
                        if (pos == -1) {
                            name = str;
                            msg = "";
                        } else {
                            name = str.substring(0, pos);
                            msg = str.substring(pos + 1);
                        }

                        if (!lookupAndSendMsg(name, msg)) {
                            System.out.println("'" + name + "' is not currently online or is unavailable.");
                            continue;
                        }

                    } else if (cmd.toLowerCase().trim().startsWith("friends")) {
                        widgetsResourceURL = APPLICATION_URI + "/users";
                        request = new Request(Method.GET, widgetsResourceURL);

                        // We need to ask specifically for JSON
                        request.getClientInfo().getAcceptedMediaTypes().
                                add(new Preference(MediaType.APPLICATION_JSON));

                        // Now we do the HTTP GET
//                        System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
                        resp = new Client(Protocol.HTTP).handle(request);

                        // Let's see what we got!
                        if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                            Representation responseData = resp.getEntity();

                            try {
                                String jsonString = responseData.getText().toString();
                                JSONArray jsonArray = new JSONArray(jsonString);
                                System.out.println("\nThe following users are logged on:\n");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    String userName = jObj.getString("userName");
//                                    String host = jObj.getString("host");
//                                    int port = jObj.getInt("port");
                                    boolean status = jObj.getBoolean("status");
                                    System.out.println(userName + (status ? " (available)" : " (away)"));
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (JSONException je) {
                                je.printStackTrace();
                            }
                        }
                    } else if (cmd.toLowerCase().trim().startsWith("broadcast")) {

                        int pos = cmd.indexOf(' ');
                        if (pos == -1) {
                            System.out.println("Missing the message.  Enter: broadcast {msg}");
                            continue;
                        }
                        String msg = cmd.substring(pos + 1);

                        widgetsResourceURL = APPLICATION_URI + "/users";
                        request = new Request(Method.GET, widgetsResourceURL);

                        // We need to ask specifically for JSON
                        request.getClientInfo().getAcceptedMediaTypes().
                                add(new Preference(MediaType.APPLICATION_JSON));

                        // Now we do the HTTP GET
//                        System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
                        resp = new Client(Protocol.HTTP).handle(request);

                        // Let's see what we got!
                        if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                            Representation responseData = resp.getEntity();

                            try {
                                String jsonString = responseData.getText().toString();
                                JSONArray jsonArray = new JSONArray(jsonString);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    String userName = jObj.getString("userName");
                                    String host = jObj.getString("host");
                                    int port = jObj.getInt("port");
                                    boolean status = jObj.getBoolean("status");
                                    RegistrationInfo client;
                                    // except current user
                                    if (!userName.equals(this.regInfo.getUserName())) {
                                        System.out.print("Sending message to " + userName + " ... ");
                                        client = new RegistrationInfo(userName,host, port, status);
                                        if (!this.sendMsgToKnownUser(client, msg)) {
                                            System.out.println("failed (unavailable).");
                                        } else {
                                            System.out.println("Done!");
                                        }
                                    }

                                    System.out.println(userName + (status ? " (available)" : " (away)"));
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (JSONException je) {
                                je.printStackTrace();
                            }
                        }

                    } else if (cmd.toLowerCase().trim().startsWith("busy")) {
                        if (this.regInfo.getStatus()) {
                            this.regInfo.setStatus(false);
                            Form form = new Form();
                            form.add("name", this.regInfo.getUserName());
                            form.add("port", Integer.toString(this.serviceSkt.getLocalPort()));
                            form.add("status", String.valueOf(false));
                            form.add("host", this.regInfo.getHost());
                            widgetsResourceURL = APPLICATION_URI + "/users/" + this.regInfo.getUserName();
                            request = new Request(Method.PUT, widgetsResourceURL);
                            request.setEntity(form.getWebRepresentation());

                            // Now we do the HTTP GET
                            resp = new Client(Protocol.HTTP).handle(request);

                            // Let's see what we got!
                            if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                                System.out.println("Status currently set to unavailable");

                            }
                        }
                    } else if (cmd.toLowerCase().trim().startsWith("available")) {
                        if (!this.regInfo.getStatus()) {
                            this.regInfo.setStatus(true);
                            Form form = new Form();
                            form.add("name", this.regInfo.getUserName());
                            form.add("port", Integer.toString(this.serviceSkt.getLocalPort()));
                            form.add("status", String.valueOf(true));
                            form.add("host", this.regInfo.getHost());
                            widgetsResourceURL = APPLICATION_URI + "/users/" + this.regInfo.getUserName();
                            request = new Request(Method.PUT, widgetsResourceURL);
                            request.setEntity(form.getWebRepresentation());

                            // Now we do the HTTP GET
                            resp = new Client(Protocol.HTTP).handle(request);

                            // Let's see what we got!
                            if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                                System.out.println("Status currently set to available");
                            }
                        }


                    } else if (cmd.toLowerCase().trim().startsWith("exit")) {

                        // we need to unregister from name server.
                        widgetsResourceURL = APPLICATION_URI + "/users/" + this.regInfo.getUserName();
                        request = new Request(Method.DELETE, widgetsResourceURL);
                        // We need to ask specifically for JSON
                        request.getClientInfo().getAcceptedMediaTypes().
                                add(new Preference(MediaType.APPLICATION_JSON));

                        // Now we do the HTTP GET
//                        System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
                        resp = new Client(Protocol.HTTP).handle(request);
                        // Let's see what we got!
                        if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                            System.out.println("Logging off.");
                        }
                        this.svrThread.stop();
                        System.out.println("Goodbye.");
                        done = true;
                    } else {
                        System.out.println("Hmm, not sure what you meant there. Try again.");
                    }

                }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }


    /**
     * Send a message to a user with the given name.
     * @param userName The name of the user you wish to send the message to.
     * @param msg The msg string you wish to send the user.
     * @return true if the message was sent, false otherwise.
     */
    private boolean lookupAndSendMsg(String userName, String msg)
    {
        boolean retval = true;
        try {
                // look up this user's registration info so we can send message.
                String widgetsResourceURL = APPLICATION_URI + "/users/" + userName;
                Request request = new Request(Method.GET, widgetsResourceURL);

                // We need to ask specifically for JSON
                request.getClientInfo().getAcceptedMediaTypes().
                        add(new Preference(MediaType.APPLICATION_JSON));

                // Now we do the HTTP GET
                System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
                Response resp = new Client(Protocol.HTTP).handle(request);

                // Let's see what we got!
                if (resp.getStatus().equals(Status.SUCCESS_OK)) {
                    Representation responseData = resp.getEntity();
//                    System.out.println("Status = " + resp.getStatus());
                    try {
                        String jsonString = responseData.getText().toString();
//                        System.out.println("result text=" + jsonString);
                        JSONObject jObj = new JSONObject(jsonString);
                        userName = jObj.getString("userName");
                        String host = jObj.getString("host");
                        int port = jObj.getInt("port");
                        boolean status = jObj.getBoolean("status");
//                        System.out.print(msg);
                        RegistrationInfo reg = new RegistrationInfo(userName,host,port,status);
                        retval = this.sendMsgToKnownUser(reg, msg);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
            } catch(Exception re){
                System.out.println("Could not connect to presence server!");
                retval = false;
            }

        return retval;
    }

    /**
     * Send a message to a user with the given RegistrationInfo.
     * @param reg The RegistrationInfo of the user you wish to send the message to.
     * @param msg The msg string you wish to send the user.
     * @return true if the message was sent, false otherwise.
     */
    private boolean sendMsgToKnownUser(RegistrationInfo reg, String msg)
    {
        boolean retval = true;
        if(reg==null || !reg.getStatus()) {
            // User is not registered!
            retval = false;
        } else {
            try {
                // open a socket connection remote user's client and send message.
                Socket skt = new Socket(reg.getHost(),reg.getPort());
                String completeMsg = "Message from " + this.regInfo.getUserName() + ": " + msg + "\n";
                skt.getOutputStream().write(completeMsg.getBytes());
                skt.close();
            } catch (Exception e) {
                // hmmm, user was registered, but it looks like they suddenly went away.
                //e.printStackTrace();
                retval = false;
            }
        }
        return retval;
    }

    private void promptUser()
    {
        if(this.regInfo.getStatus()) {
            System.out.println(this.regInfo.getUserName() + ": Enter command (friends, chat, broadcast, busy, or exit):");
        } else {
            System.out.println(this.regInfo.getUserName() + ": Enter command (friends, chat, broadcast, available, or exit):");
        }
    }
    /**
     * Simple inner class that implements the thread that will be responsible
     * for handling incoming chat messages.
     */
    class SvrThread implements Runnable
    {
        // We'll use this to flag when the thread can stop accepting new
        // connections and exit.
        boolean done = false;

        /**
         * Thread's entry point.
         */
        public void run()
        {

            //
            // wait for incoming requests.
            //
            while(!done) {
                Socket clientSocket = null;
                try {
                    clientSocket = serviceSkt.accept();
                } catch (IOException e) {
                    System.out.println("Error: failed to accept remote connection.");
                }


                //
                // Might have shutdown while  waiting for request...
                //
                if(done) {
                    break;
                }

                //
                // Process incoming chat message right here.
                //
                byte buf[] = new byte[2048];
                try {
                    int cnt = clientSocket.getInputStream().read(buf,0,2048);
                    String msg = new String(buf,0,cnt);

                    // We'll refresh the prompt, lest the chimp on the console
                    // get's confused.
                    System.out.println(msg);
                    ChatClient.this.promptUser();
                    clientSocket.close();

                } catch (IOException ie) {
                }

            }

            // ok, we're outta here.  Turn the lights out before you leave.
            try {
                ChatClient.this.serviceSkt.close();
            } catch (IOException e) {
                System.out.println("Warning: caught IOException while closing socket.");
            }
            System.out.println("Server thread is exiting.");
        }


        /**
         * This is how we signal the ServerSocket thread which is likely to
         * be happily camped out on an accept() when the chimp types exit.
         */
        public void stop()
        {
            // set done to true.
            done = true;

            //
            // Just in case svr thread is blocked on accept, we give it a nudge.
            //
            Socket skt;
            try  {
                skt = new Socket(InetAddress.getLocalHost(),ChatClient.this.regInfo.getPort());
                skt.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Main routine for client process.
     */
    public static void main(String[] args)
    {
        System.out.print("Plese enter username: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String userName;
        try {
            userName = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
//        if(args.length == 0) {
//            System.out.println("Usage: \n\tjava ChatClient userName [host[:port]]\n");
//            System.out.println("\nwhere userName is your username, and host/port is the name service.");
//            System.exit(0);
//        }

        // Create a client object.
        ChatClient myClient;
//        if(args.length > 1) {
//            myClient = new ChatClient(userName, args[1]);
//        } else {
            myClient = new ChatClient(userName,null);
//        }

        // Now we will process chat commands.
        myClient.runCmdShell();
    }
}
