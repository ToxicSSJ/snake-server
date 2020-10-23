package me.itoxic.snake.thread;

import me.itoxic.snake.client.CommandReader;

import java.io.*;
import java.net.Socket;

public class ClientEmitter implements Runnable {

    @Override
    public void run() {

        try {

            Socket socketCliente = new Socket("localhost", 4000);
            CommandReader commandReader = new CommandReader();

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            DataOutputStream output = new DataOutputStream(socketCliente.getOutputStream());

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String linea;

            try {

                while (true) {

                    linea = stdIn.readLine();
                    commandReader.read(linea, output);

                    // output.writeUTF(linea);
                    while((linea = entrada.readLine()) != null) {
                        System.out.println("Respuesta servidor: " + linea);
                    }

                    if (linea.equals("Adios")) break;

                }

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }

            output.close();
            entrada.close();
            stdIn.close();
            socketCliente.close();

        } catch (IOException e) {
            System.err.println("No puede establer canales de E/S para la conexión");
            System.exit(-1);
        }

    }

}
