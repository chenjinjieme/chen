package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/装備/{equipment}")
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("/编辑")
    public ResponseEntity<?> update(@PathVariable("equipment") String equipment, String own) {
        return equipmentService.update(equipment, own);
    }

    @GetMapping("/icon")
    public ResponseEntity<?> icon(@PathVariable("equipment") String equipment) {
        return equipmentService.icon(equipment);
    }
}
