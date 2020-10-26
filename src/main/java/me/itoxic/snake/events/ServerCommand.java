package me.itoxic.snake.events;

import me.itoxic.snake.server.ServerCommandReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ServerCommand {

    void process(ServerCommandReader reader, String cmd, DataInputStream input, DataOutputStream output);

}
