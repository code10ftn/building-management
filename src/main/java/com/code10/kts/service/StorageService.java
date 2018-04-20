package com.code10.kts.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for file storage.
 */
public interface StorageService {

    /**
     * Store file in storage.
     *
     * @param file file to store
     * @param id   file id
     */
    void store(MultipartFile file, long id);

    /**
     * Load file from storage.
     *
     * @param id file id
     * @return file byte array
     */
    byte[] load(long id);
}
