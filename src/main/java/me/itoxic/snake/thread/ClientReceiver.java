package me.itoxic.snake.thread;

import me.itoxic.snake.logging.Logger;
import me.itoxic.snake.server.Response;
import me.itoxic.snake.server.ServerCommandReader;
import me.itoxic.snake.server.SnakeServer;
import me.itoxic.snake.util.FileUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientReceiver implements Runnable {

    private Socket socket;
    private SnakeServer snakeServer;
    private ServerCommandReader reader;

    private DataInputStream input;
    private DataOutputStream output;

    public ClientReceiver(SnakeServer snakeServer, Socket socket) {

        this.socket = socket;
        this.snakeServer = snakeServer;

        try {

            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

        } catch(Exception e) {

            e.printStackTrace();
            Logger.error("An error occurred while connection.");

        }

    }

    @Override
    public void run() {

        try {

            String type = input.readUTF();

            if(type.equalsIgnoreCase("client")) {

                this.reader = new ServerCommandReader(snakeServer, this);

                snakeServer.receivers().add(this);
                Logger.net( "A new client has been connected: " + socket + ", there are currently " + snakeServer.receivers().size() + " clients connected.");

                while (true) {

                    while(input.available() > 0)
                        input.readByte();

                    String cmd = input.readUTF();

                    if(!reader.run(cmd, input, output)) {
                        emit(Response.BAD_CMD);
                        continue;
                    }

                }

            } else if(type.equalsIgnoreCase("files")) {

                int protocol = input.readInt();

                String bucket = input.readUTF();
                String file = input.readUTF();

                Logger.net( "A sub-channel was generated for file transfer of type (" + protocol + "), connection: " + socket);
                Logger.info("Sub-channel information [File: " + file + ", Bucket: " + bucket + ", Type: " + ((protocol == 1) ? "Upload" : "Download") + "]");

                String filePath = snakeServer.getFilePath(bucket, file);
                File baseFile = new File(filePath);

                if(protocol == 1) {

                    baseFile.mkdirs();
                    baseFile.createNewFile();

                    Long size = input.readLong();

                    FileUtil.copyInputStreamToFile(input, socket, size, baseFile);
                    Logger.info("New file uploaded from client: " + baseFile.toPath() + ", closing sub-channel...");
                    socket.close();
                    return;

                }

                byte[] fileContent;

                try {

                    fileContent = Files.readAllBytes(baseFile.toPath());

                    output.flush();
                    output.writeLong(fileContent.length); // Tamaño del archivo
                    output.write(fileContent); // Enviar archivo


                    Logger.info("File " + baseFile.getName() + " sended to client, closing sub-channel...");
                    input.close();
                    output.close();
                    socket.close();
                    return;

                } catch (IOException e) {

                    e.printStackTrace();
                    Logger.error("An error occurred while reading and sending the file.");
                    return;

                }

            }



        } catch(Exception e) {

            reader.getServer().receivers().remove(this);
            Logger.net("A client closed the connection, there are currently " + reader.getServer().receivers().size() + " clients connected.");
            return;

        }

    }

    public void emit(Response response) {
        emit(response.getCode(), response.getLiteral());
    }

    public void emit(int code, String msg) {

        try {

            output.writeInt(code);
            output.writeUTF(msg);

            output.flush();

        } catch(Exception e) {

            e.printStackTrace();
            Logger.error("An error occurred while emitting response message.");

        }

    }

}
