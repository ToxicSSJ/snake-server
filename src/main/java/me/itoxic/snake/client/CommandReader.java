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

        commands.put("delete", (reader, dataOutputStream, cmd, args) -> {

            reader.delete(args, dataOutputStream);

        });

        commands.put("deleteBucket", (reader, dataOutputStream, cmd, args) -> {

            reader.deleteBucket(args, dataOutputStream);

        });

        commands.put("createBucket", (reader, dataOutputStream, cmd, args) -> {

            reader.createBucket(dataOutputStream, args);

        });

        commands.put("listBucket", (reader, dataOutputStream, cmd, args) -> {

            reader.listBucket(args, dataOutputStream);

        });

        commands.put("download", (reader, dataOutputStream, cmd, args) -> {

            reader.download(args, dataOutputStream);

        });

        commands.put("list", (reader, dataOutputStream, cmd, args) -> {

            reader.list(args, dataOutputStream);

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

    public void delete(String line, DataOutputStream dataOutputStream) { }

    public void download(String line, DataOutputStream dataOutputStream) {}

    public void list(String line, DataOutputStream dataOutputStream) { }

    public void listBucket(String line, DataOutputStream dataOutputStream) { }

    public void deleteBucket(String line, DataOutputStream dataOutputStream) { }

    public void createBucket(DataOutputStream dataOutputStream, String path) {

        File file = new File(path);

        if(!file.isDirectory()){
            new File(path).mkdirs();
            System.out.println("Carpeta creada");
        }
        else {
            System.out.println("Carpeta ya existente");
        }
    }

}
