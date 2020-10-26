package me.itoxic.snake;

import me.itoxic.snake.logging.Logger;
import me.itoxic.snake.server.SnakeServer;
import me.itoxic.snake.thread.ClientEmitter;
import me.itoxic.snake.util.OptionBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static String DEFAULT_SERVER_PATH = "./server";
    public static String DEFAULT_SERVER_HOST = "localhost";
    public static int DEFAULT_PORT = 4000;

    public static void main(String[] args) throws IOException {

        CommandLine cmd = OptionBuilder.builder()
                .add("s", "server", false, false, "Use this instance as server")
                .add("c", "client", false, false, "Use this instance as client")
                .add("p", "port", true, false, "Port of the server")
                .add("h", "host", true, false, "Host of the server")
                .add("f", "files", true, false, "Server files route")
                .cmd("execute", args);

        if(cmd.hasOption("port")) {

            DEFAULT_PORT = Integer.parseInt(cmd.getOptionValue("port"));
            Logger.info("Application port changed to: " + DEFAULT_PORT);

        }

        if(cmd.hasOption("server")) {

            Logger.info("Executing this instance as server...");

            if(cmd.hasOption("files")) {

                DEFAULT_SERVER_PATH = cmd.getOptionValue("files");
                Logger.info("Server path changed to: " + DEFAULT_SERVER_PATH);

            }

            SnakeServer snakeServer = new SnakeServer(DEFAULT_SERVER_PATH, DEFAULT_PORT);
            snakeServer.run();
            return;

        }

        Logger.info("Executing this instance as client...");

        if(cmd.hasOption("host")) {

            DEFAULT_SERVER_HOST = cmd.getOptionValue("host");
            Logger.info("Remote server host changed to: " + DEFAULT_SERVER_HOST);

        }

        ClientEmitter clientEmitter = new ClientEmitter();
        clientEmitter.run();

    }

}