package me.itoxic.snake.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class CommandReader {

    public static Map<String, Command> commands = new HashMap<>();

    static {

        commands.put("load", (reader, dataOutputStream, cmd, args) -> {

            reader.load(dataOutputStream, args);

        });

        commands.put("hello", (reader, dataOutputStream, cmd, args) -> {

            try {
                dataOutputStream.writeUTF("HELLO!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    public void read(String line, DataOutputStream dataOutputStream) {

        for(Map.Entry<String, Command> command : commands.entrySet())
            if(line.startsWith(command.getKey())) {
                line = line.replace(command.getKey() + " ", "");
                command.getValue().execute(this, dataOutputStream, command.getKey(), line);
                return;
            }

    }

    public void load(DataOutputStream dataOutputStream, String path) {

        File file = new File(path);

        if(file.exists() && !file.isDirectory()) {

            System.out.println("Archivo Encontrado: " + file.getAbsolutePath());

            byte[] fileContent;

            try {

                fileContent = Files.readAllBytes(file.toPath());
                dataOutputStream.write(fileContent);

            } catch (IOException e) {

                e.printStackTrace();

            }

            return;

        }

        System.err.println("Archivo no valido");

    }

}
