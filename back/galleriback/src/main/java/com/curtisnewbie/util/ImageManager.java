package com.curtisnewbie.util;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.curtisnewbie.config.ManageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
// import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that manages all images discovered. It also internally schedules scanning operation based on the configuration,
 * however the actually scanning operation is implemented in {@link ImageScanner#scan()}
 * </p>
 */
@Component
public class ImageManager {

    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private final ImageScanner scanner;
    private final boolean listShuffled;
    private ConcurrentMap<Integer, Path> images = new ConcurrentHashMap<>();

    public ImageManager(ImageScanner scanner, ManageConfig managerConfig) {
        this.scanner = scanner;
        this.listShuffled = managerConfig.isListShuffled();
        scheduledScan();
    }

    // TODO: Is it really necessary to schedule the scanning?
    // @Scheduled(fixedRate = 1 * 60 * 1000) // per minute
    private void scheduledScan() {
        scanner.scan().forEach(p -> {
            images.putIfAbsent(p.hashCode(), p);
        });
    }

    /**
     * Get a list of file id that can then be used to retrieve the actual file. The returned list may be shuffled
     * depending on the configuration of {@link ManageConfig#isListShuffled()}
     *
     * @return list of file id
     * @see ImageManager#get(int)
     */
    public List<Integer> list() {
        List<Integer> paths = new ArrayList<>();
        images.keySet().forEach(l -> paths.add(l));
        if (listShuffled)
            Collections.shuffle(paths);
        return paths;
    }

    /**
     * Get a list of file id in spcified page that can then be used to retrieve the actual file
     *
     * @param page  page starting at 1
     * @param limit number of images in each page
     * @return list of file id
     * @see ImageManager#get(int)
     */
    public List<Integer> listOfPage(int page, int limit) {
        List<Integer> list = list();
        int n = list.size();
        if (limit >= n) {
            return list;
        } else {
            List<Integer> lp = new ArrayList<>();
            for (int i = (page > 1 ? page - 1 : 0) * limit; i < n && i < page * limit; i++) {
                lp.add(list.get(i));
            }
            return lp;
        }
    }

    /**
     * Get all bytes of a File by its id
     *
     * @param id id
     * @return all bytes of a file
     */
    @Deprecated
    public Optional<byte[]> getBytes(int id) {
        Path path = images.get(id);
        // path not exists
        if (path == null)
            return Optional.empty();

        if (path.toFile().exists()) {
            logger.info("Reading Image: '{}'", path.toString());
            Future<byte[]> bFuture = readAllBytes(path); // try to read all bytes in another thread
            while (!bFuture.isDone() && !bFuture.isCancelled()) ;
            if (bFuture.isDone()) {
                try {
                    byte[] bytes = bFuture.get();
                    if (bytes != null)
                        return Optional.of(bytes);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error occurred while reading bytes of a file: {}", e.toString());
                }
            }
        } else {
            // path exists but file not exists
            images.remove(id);
        }
        return Optional.empty();
    }

    /**
     * Get {@code FileSystemResource} of a File by its id
     *
     * @param id id
     * @return FileSystemResouce
     */
    public Optional<FileSystemResource> getFileResource(int id) {
        Path path = images.get(id);
        // path not exists
        if (path == null)
            return Optional.empty();

        if (path.toFile().exists()) {
            logger.info("Reading Image: '{}'", path.toString());
            return Optional.of(new FileSystemResource(path));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Read image file and transfer it to the given outputStream using nio Channel
     * @param id
     * @param outputStream
     * @throws IOException
     */
    public void readByChannel(int id, OutputStream outputStream) throws IOException {
        Path path = images.get(id);
        if (path == null || !path.toFile().exists())
            throw new FileNotFoundException("id: " + id);

        try (FileChannel fileChannel = FileChannel.open(path);
             WritableByteChannel toChannel = Channels.newChannel(outputStream);
        ) {
            fileChannel.transferTo(0, fileChannel.size(), toChannel);
        }
    }

    @Deprecated
    @Async
    private Future<byte[]> readAllBytes(Path path) {
        // logger.info("readAllBytes() Thread: {}", Thread.currentThread().getId());
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new AsyncResult<>(bytes);
        } catch (IOException e) {
            logger.error("Error occurred while reading bytes of a file: {}", e.toString());
        }
        return new AsyncResult<byte[]>(null);
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
