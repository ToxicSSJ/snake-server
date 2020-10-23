package me.itoxic.snake;

import me.itoxic.snake.server.SnakeServer;
import me.itoxic.snake.thread.ClientEmitter;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final int PORT = 4000;

    public static void main(String[] args) throws IOException {

        List<String> programArguments = Arrays.asList(args);

        if(programArguments.contains("--server")) {

            SnakeServer snakeServer = new SnakeServer(PORT);
            snakeServer.run();
            return;

        }

        ClientEmitter clientEmitter = new ClientEmitter();
        clientEmitter.run();

    }

}