package com.curtisnewbie.boundary;

import java.io.IOException;
import java.util.List;

import com.curtisnewbie.config.ManageConfig;
import com.curtisnewbie.config.PagingConstants;
import com.curtisnewbie.model.ImageModel;
import com.curtisnewbie.model.ImageModelAssembler;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.curtisnewbie.util.ImageManager;

import com.curtisnewbie.vo.LastPageReqVo;
import com.curtisnewbie.vo.NextPageReqVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * RestController for Image resources
 * </p>
 */
@RestController
@RequestMapping(path = "/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageManager imageManager;
    private final ImageModelAssembler assembler;
    private final ManageConfig manageConfig;

    public ImageController(ImageManager imageManager,
                           ImageModelAssembler assembler,
                           ManageConfig manageConfig) {
        this.imageManager = imageManager;
        this.assembler = assembler;
        this.manageConfig = manageConfig;
    }

    @LogOperation(name = "get image by id", description = "Download image by id")
    @GetMapping(path = "/id/{imgId}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imageById(@PathVariable("imgId") int imgId, HttpServletResponse resp) {
        try {
            imageManager.read(imgId, resp.getOutputStream());
        } catch (IOException e) {
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("Error occurred while transferring image data", e);
        }
    }

    @LogOperation(name = "get image list", description = "fetch image list")
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> images() {
        CollectionModel<ImageModel> models = assembler.toModels(manageConfig.isListShuffled() ?
                imageManager.listAllAndShuffle() : imageManager.listAll());
        models.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).images()).withRel("all"));
        return ResponseEntity.ok(models);
    }

    @LogOperation(name = "get image list in pages", description = "fetch image list in pages")
    @GetMapping(path = "/page/{pageNo}/limit/{perPage}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> imagesOfPage(@PathVariable("pageNo") int page,
                                                                    @PathVariable("perPage") int limit) {
        List<Integer> ids = imageManager.listOfPage(page, limit);
        if (ids.size() > 0) {
            CollectionModel<ImageModel> models = assembler.toModels(ids);
            models.add(WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).imagesOfPage(page, limit))
                    .withRel("page"));
            return ResponseEntity.ok(models);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @LogOperation(name = "get next page of image list", description = "fetch next page of image list")
    @PostMapping(path = "/page/next", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> nextPage(@RequestBody NextPageReqVo vo) {
        Integer limit = vo.getLimit();
        if (limit == null || limit <= 0 || limit > PagingConstants.MAX_PAGE_LIMIT)
            limit = PagingConstants.DEFAULT_PAGE_LIMIT;

        if (vo.getLastId() == null) {
            List<Integer> ids = imageManager.listOfPage(1, limit);
            if (ids.size() > 0) {
                CollectionModel<ImageModel> models = assembler.toModels(ids);
                models.add(WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).nextPage(vo))
                        .withRel("page"));
                return ResponseEntity.ok(models);
            } else {
                return ResponseEntity.noContent().build();
            }
        }

        List<Integer> ids = imageManager.nextPage(vo.getLastId(), limit);
        if (ids.size() > 0) {
            CollectionModel<ImageModel> models = assembler.toModels(ids);
            models.add(WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).nextPage(vo))
                    .withRel("page"));
            return ResponseEntity.ok(models);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @LogOperation(name = "get prev page of image list", description = "fetch prev page of image list")
    @PostMapping(path = "/page/prev", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> prevPage(@RequestBody LastPageReqVo vo) {
        Integer limit = vo.getLimit();
        if (limit == null || limit <= 0 || limit > PagingConstants.MAX_PAGE_LIMIT)
            limit = PagingConstants.DEFAULT_PAGE_LIMIT;

        List<Integer> ids;
        if (vo.getLastId() == null) {
            ids = imageManager.listOfPage(1, limit);
        } else {
            ids = imageManager.prevPage(vo.getLastId(), limit);
        }
        if (ids != null && !ids.isEmpty()) {
            CollectionModel<ImageModel> models = assembler.toModels(ids);
            models.add(WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).prevPage(vo))
                    .withRel("page"));
            return ResponseEntity.ok(models);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
