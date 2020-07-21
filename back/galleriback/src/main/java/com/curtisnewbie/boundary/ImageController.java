package com.curtisnewbie.boundary;

import java.util.List;
import com.curtisnewbie.model.ImageModel;
import com.curtisnewbie.model.ImageModelAssembler;
import com.curtisnewbie.util.ImageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@CrossOrigin("*")
@RestController
@RequestMapping(path = "/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageManager imageManager;
    private final ImageModelAssembler assembler;

    public ImageController(ImageManager imageManager, ImageModelAssembler assembler) {
        this.imageManager = imageManager;
        this.assembler = assembler;
    }

    @GetMapping(path = "/id/{imgId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<FileSystemResource> imageById(@PathVariable("imgId") int imgId) {
        return ResponseEntity.of(imageManager.getFileResource(imgId));
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> images() {
        CollectionModel<ImageModel> models = assembler.toModels(imageManager.list());
        models.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).images()).withRel("all"));
        return ResponseEntity.ok(models);
    }

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
}
