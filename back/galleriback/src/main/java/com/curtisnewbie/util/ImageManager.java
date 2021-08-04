package com.curtisnewbie.util;

import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author yongjie.zhuang
 */
public interface ImageManager {

    /**
     * Get a list of file id that can then be used to retrieve the actual file.
     *
     * @return list of file id
     */
    List<Integer> listAll();

    /**
     * Get a list of file id that can then be used to retrieve the actual file. The list is alwasys shuffled.
     *
     * @return list of file id
     */
    List<Integer> listAllAndShuffle();

    /**
     * Get a list of file id in spcified page that can then be used to retrieve the actual file
     *
     * @param page  page starting at 1
     * @param limit number of images in each page
     * @return list of file id
     */
    List<Integer> listOfPage(int page, int limit);

    /**
     * Get next page starting at {@code lastId} (exclusive)
     *
     * @param lastId last image id
     * @param limit  page size
     * @return list of file id
     */
    List<Integer> nextPage(int lastId, int limit);

    /**
     * Get prev page starting at {@code lastId} (inclusive)
     *
     * @param lastId last image id
     * @param limit  page size
     * @return list of file id
     */
    List<Integer> prevPage(int lastId, int limit);

    /**
     * Read image file and transfer it to the given outputStream
     *
     * @param id           imageId
     * @param outputStream outputStream
     * @throws IOException
     */
    void read(int id, OutputStream outputStream) throws IOException;
}
