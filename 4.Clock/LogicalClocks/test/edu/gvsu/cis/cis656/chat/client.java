package edu.gvsu.cis.cis656.chat;

import edu.gvsu.cis.cis656.clock.VectorClock;
import edu.gvsu.cis.cis656.message.Message;
import edu.gvsu.cis.cis656.message.MessageComparator;
import edu.gvsu.cis.cis656.message.MessageTypes;
import edu.gvsu.cis.cis656.queue.PriorityQueue;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class client {

    protected static DatagramSocket socket = null;
    protected static int pid;
    protected static PriorityQueue<Message> queue;
    protected static VectorClock clock;
    private static InetAddress localHost;

    private static ReceivingThread recThread;

    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        Scanner scanner = new Scanner(System.in);
        queue = new PriorityQueue<>(new MessageComparator());
        clock = new VectorClock();


        System.out.print("Please enter a username: ");
        String username = scanner.nextLine();
        localHost = InetAddress.getLocalHost();

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        boolean success = registerUser(username);
        clock.addProcess(pid, 0);

        if(!success) {
            return;
        }

        recThread = new ReceivingThread(socket, queue, clock);
        Thread thread  = new Thread(recThread);
        thread.start();

        boolean running = true;
        int sendNumber = 1;
        while (running) {
            System.out.print("Type 'exit' or a message: ");
            String line = scanner.nextLine();

            if(line.equals("exit")) {
                recThread.stop();
                Thread.sleep(500);
                running = false;
            } else {
                clock.tick(pid); // Update clock
                Message message = new Message(MessageTypes.CHAT_MSG, username, pid, clock, " (" + sendNumber +  "): " + line);
                Message.sendMessage( message, socket, localHost, 8000);
                sendNumber++;
            }
        }

        System.exit(0);
    }

    private static boolean registerUser(String username)
    {
        try {

            // Send registration
            Message message = new Message(MessageTypes.REGISTER, username, 0, null, "reg");
            Message.sendMessage(message, socket, localHost, 8000);

            // Receive something back
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            Message returned = Message.parseMessage(received);

            if(returned.type == MessageTypes.ACK) {
                System.out.println(returned.message);
                pid = returned.pid;
            }

            return returned.type == MessageTypes.ACK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class ReceivingThread implements Runnable {

        private DatagramSocket socket;
        PriorityQueue<Message> queue;
        VectorClock localClock;
        int myPid;

        public ReceivingThread(DatagramSocket socket, PriorityQueue<Message> queue, VectorClock localClock) {
            this.socket = socket;
            this.queue = queue;
            this.localClock = localClock;
        }

        boolean done = false;

        public void run() {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (!done) {

                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (done)
                    socket.close();

                String received = new String(packet.getData(), 0, packet.getLength());
                Message message = Message.parseMessage(received);

                if (message.type == MessageTypes.ERROR) {
                    System.out.println(message.message);
                } else {
                    queue.add(message);
                    Message topMsg = queue.peek();
                    while (topMsg != null) {
                        if (canPrint(topMsg)) {
                            System.out.println("\n" +topMsg.sender + topMsg.message);
                            System.out.print("Type 'exit' or a message: ");

                            // Update local clock
                            localClock.update(topMsg.ts);

                            // Remove from queue
                            queue.remove(topMsg);
                            topMsg = queue.peek();
                        } else {
                            topMsg = null;
                        }
                    }
                }
            }

            socket.close();

            System.out.println("Server thread is exiting.");
        }

        public void stop() {
            done = true;
        }

        private boolean canPrint(Message message) {

            // Is message one ahead of my clock
            boolean isPlusOne = localClock.getTime(message.pid) + 1 == message.ts.getTime(message.pid);
            boolean hasSeenAllMessage = localClock.happenedBefore(message.ts);

            return isPlusOne && hasSeenAllMessage;
        }
    }
}