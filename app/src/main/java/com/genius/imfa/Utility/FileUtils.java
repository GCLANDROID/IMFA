package com.genius.imfa.Utility;

import java.io.File;

public class FileUtils {
    public static String checkFileSize(String filePath){
        File file = new File(filePath);
        String fileSize;
        if (file.exists()) {
            long fileSizeInBytes = file.length();
            double fileSizeInKB = fileSizeInBytes / 1024.0;
            double fileSizeInMB = fileSizeInKB / 1024.0;

            fileSize = "File size: " + fileSizeInBytes + " bytes (" +
                    String.format("%.2f", fileSizeInKB) + " KB, "+
                    String.format("%.2f", fileSizeInMB) + " MB)";
        } else {

            fileSize = "File not found or inaccessible";
        }
        return fileSize;
    }
}
