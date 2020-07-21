package com.chen.aigis.controller;

import com.chen.aigis.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/url")
public class URLController {
    @Autowired
    private URLService urlService;

    @PostMapping
    public ResponseEntity<?> add(@RequestParam MultipartFile file) throws IOException {
        return urlService.add(file);
    }

    @GetMapping
    public ResponseEntity<?> update() throws IOException {
        return urlService.update();
    }
}
