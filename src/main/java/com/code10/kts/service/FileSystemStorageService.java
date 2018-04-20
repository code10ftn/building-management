package com.code10.kts.service;

import com.code10.kts.controller.exception.BadRequestException;
import com.code10.kts.controller.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class for storing files on file system.
 */
@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage-location}")
    private String path;

    @Override
    public void store(MultipartFile file, long id) {
        final String newUrl = String.format("%s/%d.jpg", path, id);
        final File newPhoto = new File(newUrl);
        try {
            if (newPhoto.exists()) {
                Files.delete(newPhoto.toPath());
            }
            Files.createFile(newPhoto.toPath());
        } catch (IOException e) {
            throw new BadRequestException("Failed to store image");
        }

        try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newPhoto))) {
            final byte[] bytes = file.getBytes();
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store image");
        }
    }

    @Override
    public byte[] load(long id) {
        try {
            final File imgPath = new File(String.format("%s/%d.jpg", path, id));
            return Files.readAllBytes(imgPath.toPath());
        } catch (IOException e) {
            throw new NotFoundException("Image not found");
        }
    }
}
