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
 * Class that is responsible for scanning supported images
 */
@Component
@ApplicationScope
public class ImageScanner {
    /** list of image format that may be supported by html img */
    private static final String[] IMAGE_EXT_LIST = { "jpeg", "jpg", "png", "apng", "svg", "bmp" };
    private static final Set<String> IMAGE_EXT_SET;
    /** Max len among the names of supported image formats */
    private static final int IMAGE_EXT_MAXLEN;
    private static final Logger logger = LoggerFactory.getLogger(ImageScanner.class);
    private Path dir = null;
    private final AtomicBoolean initialised = new AtomicBoolean(false);

    static {
        int len = 0;
        for (String s : IMAGE_EXT_LIST)
            len = s.length() > len ? s.length() : len;
        IMAGE_EXT_MAXLEN = len;
        IMAGE_EXT_SET = new HashSet<String>(Arrays.asList(IMAGE_EXT_LIST));
    }

    public ImageScanner(ScanConfig scanConfig) {
        if (validate(scanConfig.dir())) {
            dir = Paths.get(scanConfig.dir());
            initialised.set(true);
        } else if (validate(scanConfig.defaultDir())) {
            dir = Paths.get(scanConfig.defaultDir());
            initialised.set(true);
        }

        if (isInitialised()) {
            logger.info("Image Scanner Intialised. Supported Image Formats: {}, Scanning Directory: {}",
                    IMAGE_EXT_LIST.toString(), dir.toString());
        } else {
            logger.error("Image Scanner Cannot Be Initialised, Internal Error! Aborting");
            System.exit(1);
        }
    }

    private boolean validate(String dir) {
        if (dir == null || dir.length() == 0)
            return false;

        File t = new File(dir);
        return t.exists() && t.isDirectory();
    }

    /**
     * Scan diretory
     * 
     * @return a {@code Stream} of {@code Path} or NULL if I/O error occurred.
     */
    public Stream<Path> scan() {
        try {
            return Files.walk(dir).filter(p -> {
                String ext = null;
                String absPath = p.toString();
                int len = absPath.length();
                if (len > IMAGE_EXT_MAXLEN)
                    ext = extension(absPath, len - IMAGE_EXT_MAXLEN);
                else
                    ext = extension(absPath);
                if (ext != null && IMAGE_EXT_SET.contains(ext))
                    return true;
                else
                    return false;
            });
        } catch (IOException e) {
            logger.error("Error occurred while attempting to scan directory");
            return null;
        }
    }

    public boolean isInitialised() {
        return initialised.get();
    }

    /**
     * Extract file extension
     * 
     * @param absPath
     * @param after   where the searching should end (inclusive), e.g., if
     *                {@code after} = 5, it searches within [5 :
     *                {@code absPath.length()}]
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