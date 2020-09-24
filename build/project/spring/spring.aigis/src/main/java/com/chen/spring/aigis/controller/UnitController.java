package com.chen.spring.aigis.controller;

import com.chen.spring.aigis.service.UnitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unit")
public class UnitController {
    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public ResponseEntity<?> get(String sex, String rare, String clazz) {
        return unitService.get(sex, rare, clazz);
    }

    @PostMapping
    public ResponseEntity<?> add(String id, String name, String sex, String rare, String clazz) {
        return unitService.add(id, name, sex, rare, clazz);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        return unitService.delete(id);
    }
}
