package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.UnitEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/キャラ/{unit}/rank/{rank}")
public class UnitEquipmentController {
    @Autowired
    private UnitEquipmentService unitEquipmentService;

    @PostMapping("/新增")
    public void addAll(@PathVariable("unit") String unit, @PathVariable("rank") String rank) {
        unitEquipmentService.addAll(unit, rank);
    }

    @PostMapping("/order/{order}/编辑")
    public void update(@PathVariable("unit") String unit, @PathVariable("rank") String rank, @PathVariable("order") String order, String equipment) {
        unitEquipmentService.update(unit, rank, order, equipment);
    }

    @PostMapping("/order/{order}/装備")
    public void set(@PathVariable("unit") String unit, @PathVariable("rank") String rank, @PathVariable("order") String order) {
        unitEquipmentService.set(unit, rank, order);
    }
}
