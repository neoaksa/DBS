package client;

import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import chat.PresenceService;
import chat.RegistrationInfo;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Random;

public class EchoClient {

    public static void main(String args[]) {
         int randomNumber = new Random().nextInt(9000) + 1000;
         String RMIhost = args[0];
         String userName = args[1];
         threadSocket threadA = new threadSocket(randomNumber);
         threadUI threadB = new threadUI(randomNumber,userName,RMIhost);
         threadA.start();
         threadB.start();
    }
    
        public static class threadSocket extends Thread{
            int port;
            public threadSocket(int port){
                this.port = port;
            }
            
            public void run(){
                ServerSocket echoServer = null;
                Socket clientSocket = null;
                try {

                    echoServer = new ServerSocket(port);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }

                while(true) {
                    try {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
//                         System.out.println("Open port:"+Integer.toString(port));
//                         System.out.println("Waiting for connections");
                        clientSocket = echoServer.accept();
                        Thread thread  = new Thread(new ProcessIncomingRequest(clientSocket));
                        thread.start();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
    
        public static class threadUI extends Thread{
            int port; 
            String userName;
            String RMIhost;
            
            public threadUI(int port, String userName, String RMIhost){
                this.port = port;
                this.userName=userName;
                this.RMIhost = RMIhost;
            }
            
            public void run(){
                if (System.getSecurityManager() == null) {
                    System.setSecurityManager(new SecurityManager());
                }
                try {
                    
                    // initial client info
                    String name = "chatServer";
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    String host = inetAddress.getHostAddress();
                    Registry registry = LocateRegistry.getRegistry(RMIhost);
                    PresenceService comp = (PresenceService) registry.lookup(name);
                    RegistrationInfo register = new RegistrationInfo(userName,host, port, true);
                    // regist client to server
                    if(comp.register(register)){
                        System.out.println("regist client sucessfully!");
                    }
                    else{
                        System.out.println("regist client failure!System will exit in 3 second.");
                        TimeUnit.SECONDS.sleep(3);
                        System.exit(0);
                    }
                    
                    //loop for input
                    Scanner input = new Scanner(System.in);
                    String inputStr;
                    while(true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("     ");
                        System.out.println("please input your option:");
                        System.out.println("[friends] : list all available friends.");
                        System.out.println("[talk]{username}{message} : talk to friends.");
                        System.out.println("[broadcast]{message} : broadcast message.");
                        System.out.println("[busy] : set status as busy.");
                        System.out.println("[available] : set status as available.");
                        System.out.println("[exit] : exit program");
                        
                        inputStr = input.nextLine();
                        // tokensize
                        StringTokenizer st = new java.util.StringTokenizer(inputStr,"{}");
                        // create ArrayList object
                        ArrayList<String> elements = new ArrayList<String>();
                
                        // iterate through StringTokenizer tokens
                        while(st.hasMoreTokens()) {
                
                            // add tokens to AL
                            elements.add(st.nextToken());
                        }
                        // list all friend info
                        if(elements.get(0).equals("friends")){
                            Vector<RegistrationInfo> clients = comp.listRegisteredUsers();
                            if(clients==null){
                                System.out.println("There is no available user at this moment.");
                            }
                            else{
                                System.out.println("user:status:port");
                                for(RegistrationInfo client: clients){
                                    if(client.getStatus())
                                        System.out.println(client.getUserName()+": available :"+client.getPort());
                                    else
                                        System.out.println(client.getUserName()+": busy :"+client.getPort());
                                }
                            }
                        }
                        else if(elements.get(0).equals("talk")){
                            
                            //format is error
                            if(elements.size()!=3){
                                System.out.println("talk format error!");
                            }
                            else{
                                
                            
                                RegistrationInfo stockServer = comp.lookup(elements.get(1));
                                if(stockServer==null){
                                    System.out.println("user didn't exist!");
                                }
                                else if(!stockServer.getStatus()){
                                    System.out.println("user is busy.");
                                }
                                else{
                                    stockMsg(stockServer.getHost(),stockServer.getPort(),userName+":"+elements.get(2));
                                    System.out.println("message sent out!");
                                }
                            }
                        }
                        else if(elements.get(0).equals("broadcast")){
                            //format is error
                            if(elements.size()!=2){
                                System.out.println("broadcast format error!");
                            }
                            else{
                        
                                Vector<RegistrationInfo> clients = comp.listRegisteredUsers();
                                if(clients==null){
                                    System.out.println("There is no available user at this moment.");
                                }
                                else{
                                    for(RegistrationInfo stockServer: clients){
                                        if(stockServer.getStatus()){
                                            stockMsg(stockServer.getHost(),stockServer.getPort(),userName+":"+elements.get(1));
                                        }
                                    }
                                    System.out.println("message sent out!");
                                }
                            }
                        }
                        else if(elements.get(0).equals("busy")){
                            register.setStatus(false);
                            comp.updateRegistrationInfo(register);
                            System.out.println("change status to busy!");
                        }
                        else if(elements.get(0).equals("available")){
                            register.setStatus(true);
                            comp.updateRegistrationInfo(register);
                            System.out.println("change status to available!");
                        }
                        else if(elements.get(0).equals("exit")){
                            comp.unregister(userName);
                            System.exit(0);
                        }
                        else{
                            System.out.println("input error. please enter again");
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Client exception:");
                    e.printStackTrace();
                }
        }
        
            public void stockMsg(String host, int port, String message){
                try {
                    String line;
                    BufferedReader is, server;
                    PrintStream os;

                    System.out.println("Client is attempting connections");
                    Socket clientSocket = new Socket(host, port);

        //             is = new BufferedReader(new InputStreamReader(System.in) );
                    os = new PrintStream(clientSocket.getOutputStream());
        //             server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //             while(true) {
        //                 System.out.print("Please enter a message, or enter 'quit' to exit: ");
        //                 line = is.readLine();
        //                 if(line  == null) {
        //                     break;
        //                 } else if(line.equals("quit")) {
        //                     clientSocket.close();
        //                     break;
        //                 }
                        os.println(message);
        //                 line = server.readLine();
        //                 System.out.println("Received back: " + line);
        //             }

                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        //         System.out.println("Client is exiting");
            }
        }
}

