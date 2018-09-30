package edu.gvsu.cis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ProcessIncomingRequest implements Runnable {
    private Socket clientSocket;

    public ProcessIncomingRequest(Socket clientSocket) {
        super();
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String line;
        BufferedReader is;
        PrintStream os;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());

            while(true) {
                line = is.readLine();
                if(line == null) {
                    break;
                }
                System.out.println("Received " + line);
                os.println(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
