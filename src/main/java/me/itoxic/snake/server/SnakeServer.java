package me.itoxic.snake.server;

import me.itoxic.snake.logging.Logger;
import me.itoxic.snake.thread.ClientReceiver;
import me.itoxic.snake.util.TimeUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SnakeServer {

    private List<ClientReceiver> receivers;
    private Date startDate;

    private String serverPath;
    private int port;

    public SnakeServer(String serverPath, int port) {

        this.receivers = new ArrayList<>();
        this.startDate = new Date();

        this.serverPath = serverPath;
        this.port = port;

    }

    public void run() throws IOException {

        ServerSocket socketServidor = null;

        try {
            socketServidor = new ServerSocket(port);
            socketServidor.setSoTimeout(1000 * 960);
        } catch (IOException e) {
            System.out.println("No puede escuchar en el puerto: " + port);
            System.exit(-1);
        }

        Socket socketClient = null;
        Logger.net( "Server opened in " + socketServidor + "!");

        try {

            while(true) {

                socketClient = socketServidor.accept();

                ClientReceiver clientReceiver = new ClientReceiver(this, socketClient);

                Thread thread = new Thread(clientReceiver);
                thread.start();

            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        socketClient.close();
        socketServidor.close();

    }

    public boolean hasBucket(String bucket) {

        String bucketPath = serverPath + "/" + bucket + "/";
        File file = new File(bucketPath);

        return file.isDirectory();

    }

    public boolean hasFile(String bucket, String fileName) {

        String filePath  = serverPath + "/" + bucket + "/" + fileName;
        File file = new File(filePath);

        return file.exists();

    }

    public String getServerPath() {
        return serverPath;
    }

    public String getBucketPath(String bucket) {
        return serverPath + "/" + bucket + "/";
    }

    public String getFilePath(String bucket, String file) {
        return serverPath + "/" + bucket + "/" + file;
    }

    public String getUptime() {
        Long seconds = (new Date().getTime() - startDate.getTime()) / 1000;
        return TimeUtil.formatSeconds(seconds.intValue());
    }

    public List<ClientReceiver> receivers() {
        return receivers;
    }

}
