package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.UnitEquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/キャラ/{unit}/rank/{rank}")
public class UnitEquipmentController {
    private final UnitEquipmentService unitEquipmentService;

    public UnitEquipmentController(UnitEquipmentService unitEquipmentService) {
        this.unitEquipmentService = unitEquipmentService;
    }

    @PostMapping("/新增")
    public ResponseEntity<?> addAll(@PathVariable("unit") String unit, @PathVariable("rank") String rank) {
        return unitEquipmentService.addAll(unit, rank);
    }

    @PostMapping("/order/{order}/编辑")
    public ResponseEntity<?> update(@PathVariable("unit") String unit, @PathVariable("rank") String rank, @PathVariable("order") String order, String equipment) {
        return unitEquipmentService.update(unit, rank, order, equipment);
    }

    @PostMapping("/order/{order}/装備")
    public ResponseEntity<?> set(@PathVariable("unit") String unit, @PathVariable("rank") String rank, @PathVariable("order") String order) {
        return unitEquipmentService.set(unit, rank, order);
    }
}
