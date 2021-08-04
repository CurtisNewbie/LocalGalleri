package com.curtisnewbie.util;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.google.common.collect.TreeRangeMap;
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
 * Class that manages all images discovered. Once the server is up, the images discovered won't be changed. I.e., the
 * Map of images are unmodifiable.
 * </p>
 */
@Component
public class ImageManagerImpl implements ImageManager {

    private static final Logger logger = LoggerFactory.getLogger(ImageManagerImpl.class);
    private final ImageScanner scanner;

    /** imageId to path, unmodifiable */
    private final NavigableMap<Integer, Path> unmodifiableImgMap;

    public ImageManagerImpl(ImageScanner scanner) {
        this.scanner = scanner;
        NavigableMap<Integer, Path> m = new TreeMap<>();

        // we are adding images sequentially in a single thread, just increment the id is enough
        scanner.scan().forEach(new ImageIdRecorder(m));
        unmodifiableImgMap = Collections.unmodifiableNavigableMap(m);
    }

    /**
     * Get a list of file id that can then be used to retrieve the actual file.
     *
     * @return list of file id
     */
    @Override
    public List<Integer> listAll() {
        List<Integer> paths = new ArrayList<>();
        unmodifiableImgMap.keySet().forEach(l -> paths.add(l));
        return paths;
    }

    /**
     * Get a list of file id that can then be used to retrieve the actual file. The list is alwasys shuffled.
     *
     * @return list of file id
     */
    @Override
    public List<Integer> listAllAndShuffle() {
        List<Integer> list = listAll();
        Collections.shuffle(list);
        return list;
    }

    /**
     * Get a list of file id in spcified page that can then be used to retrieve the actual file
     *
     * @param page  page starting at 1
     * @param limit number of images in each page
     * @return list of file id
     */
    @Override
    public List<Integer> listOfPage(int page, int limit) {
        List<Integer> list = listAll();
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

    @Override
    public List<Integer> nextPage(int lastId, int limit) {
        if (!unmodifiableImgMap.containsKey(lastId)) {
            return new ArrayList<>();
        }
        NavigableMap<Integer, Path> tailMap = unmodifiableImgMap.tailMap(lastId, false);
        List<Integer> l = new LinkedList<>();
        int i = 0;
        for (Map.Entry<Integer, Path> e : tailMap.entrySet()) {
            if (i >= limit)
                break;
            i++;
            l.add(e.getKey());
        }
        return l;
    }

    @Override
    public List<Integer> prevPage(int lastId, int limit) {
        if (!unmodifiableImgMap.containsKey(lastId)) {
            return new ArrayList<>();
        }
        NavigableMap<Integer, Path> tailMap = unmodifiableImgMap.headMap(lastId, false).descendingMap();
        List<Integer> l = new LinkedList<>();
        int i = 0;
        for (Map.Entry<Integer, Path> e : tailMap.entrySet()) {
            if (i >= limit)
                break;
            i++;
            l.add(e.getKey());
        }
        return l;
    }

    /**
     * Read image file and transfer it to the given outputStream using nio Channel
     *
     * @param id
     * @param outputStream
     * @throws IOException
     */
    @Override
    public void read(int id, OutputStream outputStream) throws IOException {
        Path path = unmodifiableImgMap.get(id);
        if (path == null || !path.toFile().exists())
            throw new FileNotFoundException("id: " + id);

        try (FileChannel fileChannel = FileChannel.open(path);
             WritableByteChannel toChannel = Channels.newChannel(outputStream);
        ) {
            fileChannel.transferTo(0, fileChannel.size(), toChannel);
        }
    }

    private static class ImageIdRecorder implements Consumer<Path> {

        private int lastId = 0;
        private final Map<Integer, Path> map;

        private ImageIdRecorder(Map<Integer, Path> m) {
            this.map = m;
        }

        @Override
        public void accept(Path p) {
            map.putIfAbsent(++lastId, p);
        }
    }
}
