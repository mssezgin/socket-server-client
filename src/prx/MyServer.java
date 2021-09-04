package prx;

import java.io.*;
import java.net.*;
import java.sql.*;

public class MyServer {

    public static void main(String[] args) {

        // port
        int port = 5008;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // connection
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started.");
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("ERROR :: Server could not be started.");
            return;
        }

        // database
        MyDB db = new MyDB();
        try {
            db.connectDB();
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("ERROR :: Could not connect to database.");
            // serverSocket.close();
            return;
        }

        // while loop
        while (true) {

            Socket socket = null;

            try {
                socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("New client accepted: " + socket);
                Thread thread = new MyClientHandler(socket, inputStream, outputStream, db);
                thread.start();
            } catch (IOException e) {
                System.out.println("ERROR :: Could not accept the client: " + socket);
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("ERROR :: Could not close the client: " + socket);
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }
}
