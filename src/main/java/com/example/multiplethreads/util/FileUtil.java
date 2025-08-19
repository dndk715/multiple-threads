package com.example.multiplethreads.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * 여러 파일을 ZIP으로 압축
     */
    public static byte[] createZipArchive(List<Path> files) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Path file : files) {
                if (Files.exists(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                    zos.putNextEntry(zipEntry);

                    try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file))) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            zos.write(buffer, 0, bytesRead);
                        }
                    }
                    zos.closeEntry();
                }
            }
            
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 임시 디렉토리에 파일 생성
     */
    public static Path createTempFile(String prefix, String suffix, String content) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.write(tempFile, content.getBytes());
        return tempFile;
    }

    /**
     * 임시 디렉토리 정리
     */
    public static void cleanupTempFiles(List<Path> files) {
        for (Path file : files) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                // 로그만 남기고 계속 진행
                System.err.println("임시 파일 삭제 실패: " + file + ", 오류: " + e.getMessage());
            }
        }
    }
}