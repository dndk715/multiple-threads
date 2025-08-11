package com.example.multiplethreads.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtil {

    private static final int BUFFER_SIZE = 64 * 1024; // 64KB

    public static void copyFile(Path source, Path destination) throws IOException {
        // Ensure parent directories exist
        Files.createDirectories(destination.getParent());

        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(source), BUFFER_SIZE);
             BufferedOutputStream out = new BufferedOutputStream(
                     Files.newOutputStream(destination,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE),
                     BUFFER_SIZE)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}