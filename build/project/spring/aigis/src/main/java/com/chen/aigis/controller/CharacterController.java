package com.chen.aigis.controller;

import com.chen.aigis.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/character")
public class CharacterController {
    @Autowired
    private CharacterService characterService;

    @GetMapping
    public Map<String, Object> get(@RequestParam String sex, @RequestParam String rare, @RequestParam String clazz) {
        return characterService.get(sex, rare, clazz);
    }

    @PostMapping
    public Map<String, Object> add(@RequestParam String id, @RequestParam String name, @RequestParam String sex, @RequestParam String rare, @RequestParam String clazz) {
        return characterService.add(id, name, sex, rare, clazz);
    }

    @PostMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") String id) {
        return characterService.delete(id);
    }
}
