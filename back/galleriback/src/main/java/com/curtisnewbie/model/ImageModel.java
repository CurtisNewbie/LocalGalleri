package com.curtisnewbie.model;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * ImageModel (HATEOAS model) with links
 * </p>
 */
@Relation("image")
public class ImageModel extends RepresentationModel<ImageModel> {

    private int id;

    public ImageModel(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
