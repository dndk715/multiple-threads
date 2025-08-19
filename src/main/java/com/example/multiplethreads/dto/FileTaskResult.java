package com.example.multiplethreads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileTaskResult {
    private int taskId;
    private String fileName;
    private Path filePath;
    private String fileType;
    private long fileSize;
    private boolean success;
    private String errorMessage;
    
    public FileTaskResult(int taskId, String fileName, Path filePath, String fileType, long fileSize) {
        this.taskId = taskId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.success = true;
    }
    
    public FileTaskResult(int taskId, String errorMessage) {
        this.taskId = taskId;
        this.errorMessage = errorMessage;
        this.success = false;
    }
}
