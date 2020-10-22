package me.itoxic.snake.server;

import me.itoxic.snake.thread.ClientReceiver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SnakeServer {

    private int port;

    public SnakeServer(int port) {

        this.port = port;

    }

    public void run() throws IOException {

        ServerSocket socketServidor = null;

        try {
            socketServidor = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("No puede escuchar en el puerto: " + port);
            System.exit(-1);
        }

        Socket socketCliente = null;
        System.out.println("Escuchando: " + socketServidor);

        try {

            while(true) {

                socketCliente = socketServidor.accept();
                System.out.println("Connexión acceptada: "+ socketCliente);

                ClientReceiver clientReceiver = new ClientReceiver(socketCliente);

                Thread thread = new Thread(clientReceiver);
                thread.start();

            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        socketCliente.close();
        socketServidor.close();

    }

}
