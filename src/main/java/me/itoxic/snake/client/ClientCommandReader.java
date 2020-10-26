package me.itoxic.snake.client;

import com.github.tomaslanger.chalk.Chalk;
import me.itoxic.snake.Main;
import me.itoxic.snake.events.ClientCommand;
import me.itoxic.snake.events.SnakeReader;
import me.itoxic.snake.logging.Logger;
import me.itoxic.snake.server.Response;
import me.itoxic.snake.util.FileUtil;
import me.itoxic.snake.util.OptionBuilder;
import org.apache.commons.cli.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ClientCommandReader extends SnakeReader {

    private static Map<String, ClientCommand> commands = new HashMap<>();
    private static ClientCommandReader instance;

    static {

        commands.put("developers", (cmd, line, input, output) -> {

            Logger.info("===== [ Snake ] =====");
            Logger.info("System developed by:");
            Logger.info("- Abraham M. Lora");
            Logger.info("- Camilo G. Montoya");

            Logger.info("======  =====  ======");
            return false;

        });

        commands.put("help", (cmd, line, input, output) -> {

            Logger.info("===== [ Snake ] =====");
            Logger.info("Currently commands:");

            for(String cache : commands.keySet())
                Logger.info("- " + cache);

            Logger.info("======  =====  ======");
            return false;

        });

        commands.put("load", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("f", "file", "New file name in the bucket")
                    .add("b", "bucket", "Bucket to upload the file")
                    .add("p", "path", "Local path of the file to upload")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String path = command.getOptionValue("path");
            String bucket = command.getOptionValue("bucket");
            String file = command.getOptionValue("file");

            return instance.load(input, output, path, file, bucket);

        });

        commands.put("download", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("f", "file", "Bucket filename")
                    .add("b", "bucket", "Bucket to find the file")
                    .add("d", "destination", "Local path to download the file")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String destination = command.getOptionValue("destination");
            String bucket = command.getOptionValue("bucket");
            String file = command.getOptionValue("file");

            return instance.download(input, output, destination, file, bucket);

        });

        commands.put("newb", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("n", "name", "New bucket name")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String name = command.getOptionValue("name");

            return instance.newb(input, output, name);

        });

        commands.put("deleteb", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("n", "name", "Bucket name")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String name = command.getOptionValue("name");

            return instance.deleteb(input, output, name);

        });

        commands.put("deletef", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("n", "name", "Filename to delete in the cloud")
                    .add("b", "bucket", "Bucket to find the file")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String name = command.getOptionValue("name");
            String bucket = command.getOptionValue("bucket");

            return instance.deletef(output, bucket, name);

        });

        commands.put("files", (cmd, line, input, output) -> {

            CommandLine command = OptionBuilder.builder()
                    .add("b", "bucket", "Bucket to list all files")
                    .cmd(cmd, line);

            if(command == null)
                return false;

            String bucket = command.getOptionValue("bucket");
            return instance.files(output, input, bucket);

        });

        commands.put("buckets", (cmd, line, input, output) -> instance.buckets(output, input));
        commands.put("uptime", (cmd, line, input, output) -> instance.uptime(output, input));
        commands.put("close", (cmd, line, input, output) -> instance.close(output));

    }

    public ClientCommandReader() {

        if(instance != null)
            throw new UnsupportedOperationException("Ya existe un cliente corriendo en la instancia.");

        instance = this;

    }

    @Override
    public boolean run(String cmd, DataInputStream input, DataOutputStream output) throws Exception {

        for(Map.Entry<String, ClientCommand> command : commands.entrySet())
            if(cmd.startsWith(command.getKey()))
                return command.getValue().process(command.getKey(), cmd, input, output);

        Logger.error( "A valid command was not found for the expression \"" + cmd + "\".");
        return false;

    }

    public boolean newb(DataInputStream input, DataOutputStream output, String bucket) throws IOException {

        output.writeUTF("NEWB");
        output.writeUTF(bucket);
        sended("NEWB");
        return true;

    }

    public boolean deleteb(DataInputStream input, DataOutputStream output, String bucket) throws Exception {

        output.writeUTF("DELETEB");
        output.writeUTF(bucket);
        sended("DELETEB");
        return true;

    }

    public boolean deletef(DataOutputStream output, String bucket, String fileName) throws Exception {

        output.writeUTF("DELETEF");
        output.writeUTF(bucket);
        output.writeUTF(fileName);
        sended("DELETEF");
        return true;

    }

    public boolean files(DataOutputStream output, DataInputStream input, String bucket) throws Exception {

        output.writeUTF("FILES");
        output.writeUTF(bucket);
        sended("FILES");

        int code = input.readInt();
        String msg = input.readUTF();

        Logger.net("Server Response: " + code + " " + msg);

        if(code == Response.FILES.getCode()) {

            String files = input.readUTF();

            if(files.isEmpty() || files.isBlank())
                files = "[Empty]";

            Logger.info("Files in (" + bucket + ") bucket: " + String.join(", ", files.split(",")));

        }

        return false;

    }

    public boolean buckets(DataOutputStream output, DataInputStream input) throws Exception {

        output.writeUTF("BUCKETS");
        sended("BUCKETS");

        int code = input.readInt();
        String msg = input.readUTF();
        String buckets = input.readUTF();

        if(buckets.isEmpty() || buckets.isBlank())
            buckets = "[Empty]";

        Logger.net("Server Response: " + code + " " + msg);
        Logger.info("Buckets: " + String.join(", ", buckets.split(",")));
        return false;

    }

    public boolean uptime(DataOutputStream output, DataInputStream input) throws Exception {

        output.writeUTF("UPTIME");
        sended("UPTIME");

        int code = input.readInt();
        String msg = input.readUTF();
        String uptime = input.readUTF();

        Logger.net("Server Response: " + code + " " + msg);
        Logger.info("Uptime: " + uptime);
        return false;

    }

    public boolean close(DataOutputStream output) throws Exception {

        output.writeUTF("CLOSE");
        sended("CLOSE");

        Logger.info("BYE!");
        System.exit(-1);
        return false;

    }

    public boolean load(DataInputStream input, DataOutputStream output, String path, String fileName, String bucket) {

        File file = new File(path);

        if(file.exists() && !file.isDirectory()) {

            Logger.info("File Found: " + file.getAbsolutePath() + " " + Chalk.on("[Size: " + file.length() + " bytes]").bgBlue());

            byte[] fileContent;

            try {

                fileContent = Files.readAllBytes(file.toPath());

                output.writeUTF("LOAD");
                output.writeUTF(bucket);
                output.writeUTF(fileName);

                int code = input.readInt();
                String msg = input.readUTF();

                if(code == Response.RECEIVING_FILE.getCode()) {

                    Thread thread = new Thread(() -> {

                        try {

                            Socket subChannel = new Socket(Main.DEFAULT_SERVER_HOST, Main.DEFAULT_PORT);
                            DataOutputStream coutput = new DataOutputStream(subChannel.getOutputStream());

                            coutput.writeUTF("files");
                            coutput.writeInt(1); // 1 == Upload

                            coutput.writeUTF(bucket);
                            coutput.writeUTF(fileName);

                            Logger.info("Sending file to the server...");
                            coutput.writeLong(fileContent.length);
                            coutput.write(fileContent);

                            Logger.net("File sent successfully!");

                        } catch (IOException e) {

                            Logger.error("An error occurred during file transfer. (Server upload protocol)");
                            return;

                        }


                    });

                    thread.start();
                    sended("LOAD");
                    return false;

                }

                Logger.net("Server Response: " + code + " " + msg);
                return false;

            } catch (IOException e) {

                e.printStackTrace();
                Logger.error("An error occurred while reading the file");
                return false;

            }

        }

        Logger.error("The file \"" + path + "\" was not found.");
        return false;

    }

    public boolean download(DataInputStream input, DataOutputStream output, String destination, String fileName, String bucket) {

        File file = new File(destination);

        try {

            output.writeUTF("DOWNLOAD");
            output.writeUTF(bucket);
            output.writeUTF(fileName);

            int code = input.readInt();
            String msg = input.readUTF();

            if(code == Response.FILE_FOUND.getCode()) {

                Thread thread = new Thread(() -> {

                    try {

                        Socket subChannel = new Socket(Main.DEFAULT_SERVER_HOST, Main.DEFAULT_PORT);

                        DataInputStream cinput = new DataInputStream(subChannel.getInputStream());
                        DataOutputStream coutput = new DataOutputStream(subChannel.getOutputStream());

                        coutput.writeUTF("files");
                        coutput.writeInt(0); // 0 == Download

                        coutput.writeUTF(bucket);
                        coutput.writeUTF(fileName);

                        Long size = cinput.readLong();

                        Logger.info("Downloading file from the server...");

                        if(!file.exists())
                            file.createNewFile();

                        FileUtil.copyInputStreamToFile(cinput, subChannel, size, file);
                        Logger.info("Downloaded file: " + file.getAbsolutePath() + " " + Chalk.on("[Size: " + file.length() + " bytes]").bgBlue());

                        subChannel.close();
                        cinput.close();
                        coutput.close();

                    } catch (IOException e) {

                        Logger.error("Ocurrió un error durante la transferencia de archivos. (Protocolo de subida al servidor)");
                        return;

                    }


                });

                thread.start();
                return false;

            }

            Logger.net("Server Response: " + code + " " + msg);
            return false;

        } catch(Exception e) {

            Logger.error("Internal Error");
            return false;

        }

    }

    private void sended(String cmd) {
        Logger.net("Command sended to server: " + cmd);
    }

    public static ClientCommandReader getInstance() {

        if(instance == null) {

            instance = new ClientCommandReader();
            return instance;

        }

        return instance;

    }

}
