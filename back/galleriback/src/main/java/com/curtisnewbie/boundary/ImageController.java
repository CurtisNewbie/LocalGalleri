package com.curtisnewbie.boundary;

import java.io.*;
import java.util.*;
import com.curtisnewbie.model.ImageModel;
import com.curtisnewbie.model.ImageModelAssembler;
import com.curtisnewbie.util.ImageManager;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RestController
@RequestMapping(path = "/image")
public class ImageController {

    private final ImageManager imageManager;
    private final ImageModelAssembler assembler;

    public ImageController(ImageManager imageManager, ImageModelAssembler assembler) {
        this.imageManager = imageManager;
        this.assembler = assembler;
    }

    @GetMapping(path = "/id/{imgId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> imageById(@PathVariable("imgId") int imgId) {
        return ResponseEntity.of(imageManager.get(imgId));
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ImageModel>> images() {
        CollectionModel<ImageModel> models = assembler.toModels(imageManager.list());
        models.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).images()).withRel("all"));
        return ResponseEntity.ok(models);
    }

    // TODO: Change List<File> to CollectionModel
    @GetMapping("/page")
    public ResponseEntity<List<File>> imagesOfPage(
            @RequestParam(name = "start", required = false) int start,
            @RequestParam(name = "end", required = false) int end) {
        return null;
    }
}
