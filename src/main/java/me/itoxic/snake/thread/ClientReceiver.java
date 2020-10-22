package me.itoxic.snake.thread;

import me.itoxic.snake.events.ClientListener;

import java.io.*;
import java.net.Socket;

public class ClientReceiver implements Runnable {

    private Socket socket;

    public ClientReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {

            DataInputStream stream = new DataInputStream(socket.getInputStream());

            PrintWriter salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

            while (true) {

                byte[] buffer = new byte[8192]; // or wherever you like > 0
                int count;
                while ((count = stream.read(buffer)) > 0) {
                    // out.write(buffer, 0, count);
                    // System.out.println(count);
                    System.out.println("--- LLEGO TROZO ---");
                    System.out.println(new String(buffer));
                    System.out.println("--- ---");
                }

                // System.out.println("Cliente: " + value);
                //salida.println(value);
                // if (value.equals("Adios")) break;
                continue;

            }

            // socket.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
