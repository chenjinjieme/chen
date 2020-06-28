package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.EquipmentIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EquipmentIconController {
    @Autowired
    private EquipmentIconService equipmentIconService;

    @GetMapping("/装備/{equipment}/icon")
    public String icon(@PathVariable("equipment") String equipment) {
        return equipmentIconService.icon(equipment);
    }
}
