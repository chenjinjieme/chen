package com.chen.aigis.controller;

import com.chen.aigis.service.PngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/png")
public class PngController {
    @Autowired
    private PngService pngService;

    @GetMapping
    public ResponseEntity<?> getByCharacter(@RequestParam String character) {
        return pngService.getByCharacter(character);
    }
}
