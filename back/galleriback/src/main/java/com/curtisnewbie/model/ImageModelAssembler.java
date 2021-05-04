package com.curtisnewbie.model;

import java.util.ArrayList;
import java.util.List;

import com.curtisnewbie.boundary.ImageController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

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
        try {
            return WebMvcLinkBuilder
                    .linkTo(ImageController.class.getMethod("imageById", int.class, HttpServletResponse.class)
                            , model.getId())
                    .withSelfRel();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public CollectionModel<ImageModel> toModels(List<Integer> imageIds) {
        List<ImageModel> list = new ArrayList<>(imageIds.size());
        imageIds.forEach(id -> list.add(toModel(id)));
        return CollectionModel.of(list);
    }
}
