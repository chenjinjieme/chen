package com.chen.spring.aigis.controller;

import com.chen.spring.aigis.service.URLService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/url")
public class URLController {
    private final URLService urlService;

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<?> add(MultipartFile file) throws IOException {
        return urlService.add(file);
    }

    @GetMapping
    public ResponseEntity<?> update() throws IOException {
        return urlService.update();
    }
}
