// File: com/quackstagram/util/FileUtil.java
package com.quackstagram.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for file operations
 */
public class FileUtil {
    
    /**
     * Read all lines from a file
     * 
     * @param filePath the path to the file
     * @return a list of lines
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }
    
    /**
     * Read all lines from a file that match a predicate
     * 
     * @param filePath the path to the file
     * @param predicate the predicate to match
     * @return a list of matching lines
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readMatchingLines(String filePath, Predicate<String> predicate) throws IOException {
        List<String> matchingLines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (predicate.test(line)) {
                    matchingLines.add(line);
                }
            }
        }
        return matchingLines;
    }
    
    /**
     * Write lines to a file
     * 
     * @param filePath the path to the file
     * @param lines the lines to write
     * @param append whether to append to the file
     * @throws IOException if an I/O error occurs
     */
    public static void writeLines(String filePath, List<String> lines, boolean append) throws IOException {
        Path path = Paths.get(filePath);
        
        // Ensure parent directories exist
        Files.createDirectories(path.getParent());
        
        if (append) {
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.write(path, lines);
        }
    }
    
    /**
     * Append a line to a file
     * 
     * @param filePath the path to the file
     * @param line the line to append
     * @throws IOException if an I/O error occurs
     */
    public static void appendLine(String filePath, String line) throws IOException {
        Path path = Paths.get(filePath);
        
        // Ensure parent directories exist
        Files.createDirectories(path.getParent());
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }
    
    /**
     * Update lines in a file based on a predicate
     * 
     * @param filePath the path to the file
     * @param predicate the predicate to match lines to update
     * @param updater the function to update matching lines
     * @throws IOException if an I/O error occurs
     */
    public static void updateLines(String filePath, Predicate<String> predicate, 
                                 java.util.function.Function<String, String> updater) throws IOException {
        List<String> allLines = readAllLines(filePath);
        List<String> updatedLines = new ArrayList<>();
        
        for (String line : allLines) {
            if (predicate.test(line)) {
                updatedLines.add(updater.apply(line));
            } else {
                updatedLines.add(line);
            }
        }
        
        writeLines(filePath, updatedLines, false);
    }
    
    /**
     * Copy a file
     * 
     * @param source the source file
     * @param destination the destination file
     * @param replace whether to replace existing files
     * @throws IOException if an I/O error occurs
     */
    public static void copyFile(File source, String destination, boolean replace) throws IOException {
        Path destPath = Paths.get(destination);
        
        // Ensure parent directories exist
        Files.createDirectories(destPath.getParent());
        
        if (replace) {
            Files.copy(source.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(source.toPath(), destPath);
        }
    }
    
    /**
     * Get the next available ID for a file pattern in a directory
     * 
     * @param directory the directory to search
     * @param prefix the prefix for files
     * @param extensionFilter the file extension filter
     * @return the next available ID
     * @throws IOException if an I/O error occurs
     */
    public static int getNextId(String directory, String prefix, String extensionFilter) throws IOException {
        Path dirPath = Paths.get(directory);
        
        // Ensure directory exists
        Files.createDirectories(dirPath);
        
        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, 
                prefix + "*." + extensionFilter)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(prefix.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore files that don't match the expected pattern
                    }
                }
            }
        }
        
        return maxId + 1;
    }
    
    /**
     * Check if a file exists
     * 
     * @param filePath the path to the file
     * @return true if the file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Create a file if it doesn't exist
     * 
     * @param filePath the path to the file
     * @throws IOException if an I/O error occurs
     */
    public static void createFileIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        // Ensure parent directories exist
        Files.createDirectories(path.getParent());
        
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
    
    /**
     * Delete a file
     * 
     * @param filePath the path to the file
     * @throws IOException if an I/O error occurs
     */
    public static void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }
}