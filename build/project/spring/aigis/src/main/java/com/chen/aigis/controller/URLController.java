package com.chen.aigis.controller;

import com.chen.aigis.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/url")
public class URLController {
    @Autowired
    private URLService urlService;

    @PostMapping
    public Map<String, Object> add(@RequestParam MultipartFile file) {
        return urlService.add(file);
    }

    @GetMapping
    public Map<String, Object> update() {
        return urlService.update();
    }
}
