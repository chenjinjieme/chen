package com.chen.spring.pcr.controller;

import com.chen.spring.pcr.service.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/装備")
    public String equipment(String sort, String hide, Model model) {
        return pageService.equipment(sort, hide, model);
    }

    @GetMapping("/キャラ")
    public String unit(Model model) {
        return pageService.unit(model);
    }

    @GetMapping("/キャラ/{unit}/装備")
    public String unitEquipment(@PathVariable("unit") String unit, Model model) {
        return pageService.unitEquipment(unit, model);
    }
}
