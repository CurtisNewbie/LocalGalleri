package com.curtisnewbie.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import com.curtisnewbie.config.ScanConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that is responsible for scanning supported images
 * </p>
 */
@Component
@ApplicationScope
public class ImageScanner {
    /** list of image format that may be supported by html img */
    private static final String[] IMAGE_EXT_LIST =
            {"jpeg", "jpg", "png", "apng", "svg", "bmp", "gif"};
    private static final Set<String> IMAGE_EXT_SET;
    /** Max len among the names of supported image formats */
    private static final int IMAGE_EXT_MAXLEN;
    private static final Logger logger = LoggerFactory.getLogger(ImageScanner.class);
    private Path dir = null;
    private final AtomicBoolean initialised = new AtomicBoolean(false);

    static {
        int len = 0;
        IMAGE_EXT_SET = new HashSet<String>();
        for (String s : IMAGE_EXT_LIST) {
            len = s.length() > len ? s.length() : len;
            IMAGE_EXT_SET.add(s.toLowerCase());
        }
        IMAGE_EXT_MAXLEN = len;
    }

    public ImageScanner(ScanConfig scanConfig) {
        if (validate(scanConfig.getDir())) {
            dir = Paths.get(scanConfig.getDir());
            initialised.set(true);
        } else {
            mkdir(scanConfig.getDefDir());
            if (validate(scanConfig.getDefDir())) {
                dir = Paths.get(scanConfig.getDefDir());
                initialised.set(true);
            }
        }

        if (isInitialised()) {
            logger.info(
                    "Image Scanner Intialised. Supported Image Formats:{}, Scanning Directory:'{}'",
                    Arrays.toString(IMAGE_EXT_LIST), dir.toString());
        } else {
            logger.error("Image Scanner Cannot Be Initialised, Internal Error! Aborting");
            System.exit(1);
        }
    }

    /**
     * Validate the path of dir
     * 
     * @param dir
     * @return whether the dir is valid
     */
    private boolean validate(String dir) {
        if (dir == null || dir.length() == 0)
            return false;

        File t = new File(dir);
        return t.exists() && t.isDirectory();
    }

    /**
     * Attempt to mkdir, do nothing if the path of dir is illegal (NULL or empty)
     * 
     * @param dir
     */
    private void mkdir(String dir) {
        if (dir == null || dir.length() == 0)
            return;
        new File(dir).mkdir();
    }

    /**
     * Scan diretory and return a Steam of Path, only when the ImageScanner is initialised. This can
     * be verified using {@link ImageScanner#isInitialised()}
     * 
     * @return a {@code Stream} of {@code Path} or NULL if I/O error occurred or ImageScanner not
     *         being initialised.
     */
    public Stream<Path> scan() {
        if (!isInitialised())
            return null;
        try {
            return Files.walk(dir).filter(p -> {
                String ext = null;
                String absPath = p.toString();
                int len = absPath.length();
                if (len > IMAGE_EXT_MAXLEN)
                    ext = extension(absPath, len - IMAGE_EXT_MAXLEN);
                else
                    ext = extension(absPath);
                if (ext != null && IMAGE_EXT_SET.contains(ext.toLowerCase()))
                    return true;
                else
                    return false;
            });
        } catch (IOException e) {
            logger.error("Error occurred while attempting to scan directory");
            return null;
        }
    }

    /**
     * Return Whether the ImageScanner is initialised.
     * <p>
     * It's an indicator of whether the ImageScanner is functioning. If it's not initialised,
     * {@link ImageScanner#scan()} will do nothing.
     */
    public boolean isInitialised() {
        return initialised.get();
    }

    /**
     * Extract file extension
     *
     * @param absPath
     * @param after   where the searching should end (inclusive), e.g., if {@code after} = 5, it
     *                searches within [5 : absPath.length()-1]
     * @return file extension (without '.') or NULL if not found
     */
    private String extension(String absPath, int after) {
        int n = absPath.length();
        if (n == 0 || after >= n - 1)
            return null;
        int i = -1;
        for (int j = n - 1; j >= after; j--) {
            if (absPath.charAt(j) == '.') {
                i = j;
                break;
            }
        }
        if (i == -1)
            return null;
        return absPath.substring(i + 1);
    }

    /**
     * Extract file extension
     *
     * @param absPath
     * @return file extension (without '.') or NULL if not found
     */
    private String extension(String absPath) {
        return extension(absPath, 0);
    }
}
