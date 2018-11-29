package edu.gvsu.cis.cis656.chat;

import edu.gvsu.cis.cis656.clock.VectorClock;
import edu.gvsu.cis.cis656.message.Message;
import edu.gvsu.cis.cis656.message.MessageComparator;
import edu.gvsu.cis.cis656.message.MessageTypes;
import edu.gvsu.cis.cis656.queue.PriorityQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


public class client {

    private static int myPid;
    private static InetAddress address;
    private static DatagramSocket socket = null;
    private static PriorityQueue<Message> queue;
    private static VectorClock clock;
    private static MessageRev receiveThread;
    private static int port;

    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        String userName;
        queue = new PriorityQueue<>(new MessageComparator());
        clock = new VectorClock();
        port = 8000;
        // get username
        System.out.print("Enter username: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            userName = in.readLine();
        }catch (IOException e){
            e.printStackTrace();
            return;
        }
        address = InetAddress.getLocalHost();

        // create socket
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // register user
        Message message = new Message(0, userName, 0, null, userName);
        Message.sendMessage(message, socket, address, port);
        Message ackMessage = Message.receiveMessage(socket);
        if (ackMessage.type == 1) {
            myPid = ackMessage.pid;
            clock.addProcess(myPid, 0);
            System.out.println("registered your pid#: " + myPid + " and clock#:" + clock.toString());
        }
        else if (ackMessage.type == 3) {
            System.out.println("user name has been registered.");
            System.exit(0);
        }

        //receve message thread
        receiveThread = new MessageRev();
        Thread t = new Thread(receiveThread);
        t.start();

        boolean signal = true;
        int line = 1;
        while (signal) {
            System.out.print("wait for entering message or type 'exit':");
            String input;
            in = new BufferedReader(new InputStreamReader(System.in));
            try {
                input = in.readLine();
            }catch (IOException e){
                e.printStackTrace();
                return;
            }

            if(input.equals("exit")) {
                receiveThread.stop();
                Thread.sleep(1200);
                signal = false;
            } else {
                clock.tick(myPid); // Update clock
                Message outMessage = new Message(MessageTypes.CHAT_MSG, userName, myPid, clock, " (" + line +  "): " + input);
                Message.sendMessage( outMessage, socket, address, port);
                line++;
            }
        }

        System.exit(0);
    }


    public static class MessageRev implements Runnable {

        boolean done = false;

        public void run() {
            MessageComparator mc =new MessageComparator();
            PriorityQueue<Message> queue = new PriorityQueue<Message>(mc);
            Message msg = null;
            while(true) {
                // receive the message
                msg = Message.receiveMessage(socket);
                queue.add(msg);
                //pick and remove the first element
                Message firtMsg = queue.peek();
                while (firtMsg!=null) {
                    int firtMsgTime = 0;
                    if(clock.getMap().containsKey(Integer.toString(firtMsg.pid))) { firtMsgTime = clock.getTime(firtMsg.pid); }
                    boolean loopE = true;
                    for (String key: firtMsg.ts.getMap().keySet()) {
                        if (Integer.parseInt(key) != myPid && Integer.parseInt(key) != firtMsg.pid) {
                            if (clock.getMap().containsKey(key)) {
                                if (firtMsg.ts.getTime(Integer.parseInt(key)) > clock.getTime(Integer.parseInt(key)) ) { loopE = false;}
                            }else {
                                loopE = false;
                            }
                        }
                    }
                    if (firtMsg.ts.getTime(firtMsg.pid) == firtMsgTime + 1 && loopE) {
                        System.out.println(firtMsg.sender+": "+firtMsg.message);
                        queue.poll();
                        clock.update(firtMsg.ts);
                        firtMsg = queue.peek();
                    }else {
                        firtMsg = null;
                    }
                }
            }

            //System.out.println("Server thread is exiting.");
        }

        public void stop() {
            done = true;
        }

    }
}