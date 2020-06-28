package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/装備/{equipment}")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @PostMapping("/编辑")
    public void update(@PathVariable("equipment") String equipment, String own) {
        equipmentService.update(equipment, own);
    }
}
