package edu.gvsu.cis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

    public static void main(String args[]) {
        try {
            String line;
            BufferedReader is, server;
            PrintStream os;

            System.out.println("Client is attempting connections");
            Socket clientSocket = new Socket("localhost", 9999);

            is = new BufferedReader(new InputStreamReader(System.in) );
            os = new PrintStream(clientSocket.getOutputStream());
            server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(true) {
                System.out.print("Please enter a message, or enter 'quit' to exit: ");
                line = is.readLine();
                if(line  == null) {
                    break;
                } else if(line.equals("quit")) {
                    clientSocket.close();
                    break;
                }
                os.println(line);
                line = server.readLine();
                System.out.println("Received back: " + line);
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Client is exiting");
    }
}
