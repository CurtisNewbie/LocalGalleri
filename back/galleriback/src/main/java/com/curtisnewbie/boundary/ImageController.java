package com.curtisnewbie.boundary;

import java.io.*;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for Image resources
 * 
 */
// TODO: Not implemented yet, consider changing to HAETOAES
@RestController
@RequestMapping(path = "/image")
public class ImageController {


    // TODO: Change File to EntityModel
    @GetMapping("/id/{imgId}")
    public ResponseEntity<File> imageById(@PathVariable("imgId") String id) {
        return null;
    }

    // TODO: Change List<File> to CollectionModel
    @GetMapping("/all")
    public ResponseEntity<List<File>> images() {
        return null;
    }

    // TODO: Change List<File> to CollectionModel
    @GetMapping("/page")
    public ResponseEntity<List<File>> imagesOfPage(
            @RequestParam(name = "start", required = false) int start,
            @RequestParam(name = "end", required = false) int end) {
        return null;
    }
}
