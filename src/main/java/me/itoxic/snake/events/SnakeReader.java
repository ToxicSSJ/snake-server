package me.itoxic.snake.events;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class SnakeReader {

    public abstract boolean run(String cmd, DataInputStream input, DataOutputStream output) throws Exception;

}
