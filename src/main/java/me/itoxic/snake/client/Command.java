package me.itoxic.snake.client;

import java.io.DataOutputStream;

public interface Command {

    void execute(CommandReader reader, DataOutputStream dataOutputStream, String cmd, String args);

}
