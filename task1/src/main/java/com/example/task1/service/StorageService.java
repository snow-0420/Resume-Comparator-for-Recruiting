package com.example.task1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class StorageService {

    private final Path storeLocation;

    private String jobListing;

    @Autowired
    public StorageService(StorageProperties storageProperties) {
        this.storeLocation = Paths.get(storageProperties.getLocation());
    }

    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.storeLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.storeLocation.toAbsolutePath())) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.storeLocation, 1)
                    .filter(path -> !path.equals(this.storeLocation))
                    .map(this.storeLocation::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    public Path load(String filename) {
        return storeLocation.resolve(filename);
    }

    public File loadAsFile(String filename) {
        Path path = load(filename);
        return path.toFile();
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(storeLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectories(storeLocation);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String getJobListing() {
        return jobListing;
    }

    public void setJobListing(String jobListing) {
        this.jobListing = jobListing;
    }
}
