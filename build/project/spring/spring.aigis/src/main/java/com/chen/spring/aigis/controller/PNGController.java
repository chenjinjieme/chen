package com.chen.spring.aigis.controller;

import com.chen.spring.aigis.service.PNGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/png")
public class PNGController {
    private final PNGService pngService;

    public PNGController(PNGService pngService) {
        this.pngService = pngService;
    }

    @GetMapping
    public ResponseEntity<?> getByUnit(String unit) {
        return pngService.getByUnit(unit);
    }
}
