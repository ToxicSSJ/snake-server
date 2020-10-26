package me.itoxic.snake.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileUtil {

    public static void copyInputStreamToFile(InputStream cinput, Socket subChannel, long size, File file) throws IOException {

        try(FileOutputStream coutput = new FileOutputStream(file)) {

            long current = 0;

            int read;
            byte[] bytes = new byte[1024];

            while(subChannel.isConnected()) {

                if(current == size)
                    break;

                if(cinput.available() == 0)
                    continue;

                read = cinput.read(bytes);
                coutput.write(bytes, 0, read);

                current += read;

            }

        }

    }

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        try(FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while(inputStream.available() > 0) {

                read = inputStream.read(bytes);
                outputStream.write(bytes, 0, read);

            }

        }

    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
