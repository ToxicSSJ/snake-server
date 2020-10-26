package me.itoxic.snake.events;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ClientCommand {

    boolean process(String cmd, String args, DataInputStream input, DataOutputStream output) throws Exception;

}
