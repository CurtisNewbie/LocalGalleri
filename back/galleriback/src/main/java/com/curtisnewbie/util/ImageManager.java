package com.curtisnewbie.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that manages all images discovered. It also internally schedules scanning operation based
 * on the configuration, however the actually scanning operation is implemented in
 * {@link ImageScanner#scan()}
 * </p>
 */
@Component
@ApplicationScope
public class ImageManager {

    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private final ImageScanner scanner;
    private ConcurrentMap<Integer, Path> images = new ConcurrentHashMap<>();

    public ImageManager(ImageScanner scanner) {
        this.scanner = scanner;
        scheduledScan();
    }

    @Scheduled(fixedRate = 1 * 60 * 1000) // per minute
    private void scheduledScan() {
        logger.info("Scheduled Scanning.");
        scanner.scan().forEach(p -> {
            images.putIfAbsent(p.hashCode(), p);
        });
    }

    /**
     * Get a list of file id that can then be used to retrieve the actual file
     * 
     * @return list of file id
     * @see ImageManager#get(int)
     */
    public List<Integer> list() {
        List<Integer> paths = new ArrayList<>();
        images.keySet().forEach(l -> paths.add(l));
        return paths;
    }

    /**
     * Get all bytes of a File by its id
     * 
     * @param id id
     * @return all bytes of a file
     */
    public Optional<byte[]> get(int id) {
        Path path = images.get(id);
        // path not exists
        if (path == null)
            return Optional.empty();

        if (path.toFile().exists()) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                return Optional.of(bytes);
            } catch (IOException e) {
                logger.error("Error occurred while reading bytes of a file: {}", e.toString());
            }
        } else {
            // path exists but file not exists
            images.remove(id);
        }
        return Optional.empty();
    }

    /**
     * Delete Image by its id
     * 
     * @param id id
     */
    public void delete(int id) {
        images.remove(id);
    }
}
