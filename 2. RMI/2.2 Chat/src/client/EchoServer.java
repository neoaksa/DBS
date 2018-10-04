package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class EchoServer {

    public static void main(String args[]) {
        ServerSocket echoServer = null;
        Socket clientSocket = null;
        System.out.println("Open port:"+Integer.toString(randomNumber));
        try {

            echoServer = new ServerSocket(randomNumber);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        while(true) {
            try {
                System.out.println("Open port:"+Integer.toString(randomNumber));
                System.out.println("Waiting for connections");
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
