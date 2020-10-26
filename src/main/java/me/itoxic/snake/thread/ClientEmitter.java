package me.itoxic.snake.thread;

import me.itoxic.snake.Main;
import me.itoxic.snake.client.ClientCommandReader;
import me.itoxic.snake.logging.Logger;

import java.io.*;
import java.net.Socket;

public class ClientEmitter implements Runnable {

    @Override
    public void run() {

        try {

            Socket clientSocket = new Socket(Main.DEFAULT_SERVER_HOST, Main.DEFAULT_PORT);
            ClientCommandReader commandReader = ClientCommandReader.getInstance();

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String cliLine;

            clientSocket.setSoTimeout(1000 * 960);
            output.writeUTF("client"); // Se le envía el tipo de conexión al servidor.

            Logger.info("Client initialized, now you can write comamnds.");

            while (true) {

                cliLine = stdIn.readLine();

                try {

                    boolean valid = commandReader.run(cliLine, input, output);

                    if(!valid)
                        continue;

                    int code = input.readInt();
                    String msg = input.readUTF();

                    Logger.net("Server Response: " + code + " " + msg);

                } catch(IOException e) {

                    Logger.error("Connection reset by peer, reconnecting...");
                    run();
                    return;

                } catch (Exception e) {

                    Logger.error("Internal exception, please reopen the client.");
                    return;

                }

            }

        } catch (IOException e) {

            e.printStackTrace();
            Logger.error("Unable to connect to the host, reconnecting in 5 seconds...");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException interruptedException) { interruptedException.printStackTrace(); }

            run();
            return;

        }

    }

}
