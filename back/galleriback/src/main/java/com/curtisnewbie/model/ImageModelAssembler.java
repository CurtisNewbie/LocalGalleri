package com.curtisnewbie.model;

import java.util.ArrayList;
import java.util.List;
import com.curtisnewbie.boundary.ImageController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that facilitates assembling of ImageModel
 * </p>
 */
@Component
public class ImageModelAssembler implements RepresentationModelAssembler<Integer, ImageModel> {

    @Override
    public ImageModel toModel(Integer imageId) {
        ImageModel model = new ImageModel(imageId);
        model.add(linkToSelf(model));
        return model;
    }

    public Link linkToSelf(ImageModel model) {
        return WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ImageController.class).imageById(model.getId()))
                .withSelfRel();
    }

    public CollectionModel<ImageModel> toModels(List<Integer> imageIds) {
        List<ImageModel> list = new ArrayList<>(imageIds.size());
        imageIds.forEach(id -> list.add(toModel(id)));
        return CollectionModel.of(list);
    }
}
