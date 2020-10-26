package me.itoxic.snake.server;

import me.itoxic.snake.events.ServerCommand;
import me.itoxic.snake.events.SnakeReader;
import me.itoxic.snake.logging.Logger;
import me.itoxic.snake.thread.ClientReceiver;
import me.itoxic.snake.util.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerCommandReader extends SnakeReader {

    private static Map<String, ServerCommand> commands = new HashMap<>();

    private ClientReceiver receiver;
    private SnakeServer server;

    static {

        commands.put("NEWB", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();

                if (reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_ALREADY_EXISTS);
                    return;
                }

                File file = new File(reader.getServer().getBucketPath(bucket));

                if (!file.mkdirs()) {
                    reader.getReceiver().emit(Response.INTERNAL_ERROR);
                    return;
                }

                reader.getReceiver().emit(Response.BUCKET_CREATED);
                return;

            } catch (IOException e) {

                e.printStackTrace();
                reader.getReceiver().emit(Response.INTERNAL_ERROR);

            }

        });

        commands.put("DELETEB", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();

                if (!reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_NOT_EXISTS);
                    return;
                }

                File file = new File(reader.getServer().getBucketPath(bucket));
                FileUtil.deleteDir(file);

                reader.getReceiver().emit(Response.BUCKET_DELETED);
                return;

            } catch (IOException e) {

                e.printStackTrace();
                reader.getReceiver().emit(Response.INTERNAL_ERROR);

            }

        });

        commands.put("DELETEF", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();
                String fileName = input.readUTF();

                if (!reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_NOT_EXISTS);
                    return;
                }

                File file = new File(reader.getServer().getFilePath(bucket, fileName));

                if(!file.exists()) {
                    reader.getReceiver().emit(Response.FILE_NOT_EXISTS);
                    return;
                }

                if (!file.delete()) {
                    reader.getReceiver().emit(Response.INTERNAL_ERROR);
                    return;
                }

                reader.getReceiver().emit(Response.FILE_DELETED);
                return;

            } catch (IOException e) {

                e.printStackTrace();
                reader.getReceiver().emit(Response.INTERNAL_ERROR);

            }

        });

        commands.put("BUCKETS", (reader, cmd, input, output) -> {

            File serverPath = new File(reader.getServer().getServerPath());
            String[] buckets = serverPath.list((current, name) -> new File(current, name).isDirectory());
            String response = "";

            if(buckets != null && buckets.length >= 0)
                response = String.join(",", buckets);

            reader.getReceiver().emit(Response.BUCKETS);

            try {

                output.writeUTF(response);

            } catch (IOException e) {}

            return;

        });

        commands.put("UPTIME", (reader, cmd, input, output) -> {

            String uptime = reader.getServer().getUptime();

            reader.getReceiver().emit(Response.UPTIME);

            try {

                output.writeUTF(uptime);

            } catch (IOException e) {}

            return;

        });

        commands.put("CLOSE", (reader, cmd, input, output) -> {

            String uptime = reader.getServer().getUptime();

            reader.getReceiver().emit(Response.UPTIME);

            try {

                output.writeUTF(uptime);

            } catch (IOException e) {}

            return;

        });

        commands.put("FILES", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();

                if (!reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_NOT_EXISTS);
                    return;
                }

                File bucketPath = new File(reader.getServer().getBucketPath(bucket));

                String[] files = bucketPath.list();
                String response = "";

                if(files != null && files.length >= 0)
                    response = String.join(",", files);

                reader.getReceiver().emit(Response.FILES);

                try {

                    output.writeUTF(response);

                } catch (IOException e) {}

            } catch (IOException e) {

                e.printStackTrace();
                reader.getReceiver().emit(Response.INTERNAL_ERROR);

            }

            return;

        });

        commands.put("LOAD", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();
                String fileName = input.readUTF();

                if (!reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_NOT_EXISTS);
                    return;
                }

                reader.load(input, output, bucket, fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        commands.put("DOWNLOAD", (reader, cmd, input, output) -> {

            try {

                String bucket = input.readUTF();
                String fileName = input.readUTF();

                if (!reader.getServer().hasBucket(bucket)) {
                    reader.getReceiver().emit(Response.BUCKET_NOT_EXISTS);
                    return;
                }

                reader.download(input, output, bucket, fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    public ServerCommandReader(SnakeServer snakeServer, ClientReceiver clientReceiver) {

        server = snakeServer;
        receiver = clientReceiver;

    }

    @Override
    public boolean run(String cmd, DataInputStream input, DataOutputStream output) {

        for (Map.Entry<String, ServerCommand> command : commands.entrySet())
            if (cmd.startsWith(command.getKey())) {
                cmd = cmd.replace(command.getKey() + " ", "");
                Date before = new Date();
                command.getValue().process(this, cmd, input, output);
                Logger.net("Client command (" + command.getKey() + ") processed in " + (new Date().getTime() - before.getTime()) + "ms!");
                return true;
            }

        return false;

    }

    public void load(DataInputStream input, DataOutputStream output, String bucket, String fileName) {

        File file = new File(getServer().getFilePath(bucket, fileName));

        if (file.exists()) {
            getReceiver().emit(Response.FILE_ALREADY_EXISTS);
            return;
        }

        try {

            if (!file.createNewFile()) {
                getReceiver().emit(Response.INTERNAL_ERROR);
                return;
            }

            getReceiver().emit(Response.RECEIVING_FILE);
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void download(DataInputStream input, DataOutputStream output, String bucket, String fileName) {

        File file = new File(getServer().getFilePath(bucket, fileName));

        if (!file.exists()) {
            getReceiver().emit(Response.FILE_NOT_EXISTS);
            return;
        }

        getReceiver().emit(Response.FILE_FOUND);
        return;

    }

    public SnakeServer getServer() {
        return server;
    }

    public ClientReceiver getReceiver() {
        return receiver;
    }

}
