package com.chen.aigis.controller;

import com.chen.aigis.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/character")
public class CharacterController {
    @Autowired
    private CharacterService characterService;

    @GetMapping
    public ResponseEntity<?> get(String sex, String rare, String clazz) {
        return characterService.get(sex, rare, clazz);
    }

    @PostMapping
    public ResponseEntity<?> add(String id, String name, String sex, String rare, String clazz) {
        return characterService.add(id, name, sex, rare, clazz);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        return characterService.delete(id);
    }
}
