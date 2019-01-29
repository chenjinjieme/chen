package com.chen.aigis.controller;

import com.chen.aigis.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    @Autowired
    private PageService pageService;

    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/index/%E5%A5%B3/%E3%83%96%E3%83%A9%E3%83%83%E3%82%AF/%E3%82%BD%E3%83%AB%E3%82%B8%E3%83%A3%E3%83%BC/%E5%85%89%E3%81%AE%E5%AE%88%E8%AD%B7%E8%80%85%E3%82%A2%E3%83%AB%E3%83%86%E3%82%A3%E3%82%A2/261_card_0.png";
    }

    @GetMapping({"/index/{sex}/{rare}/{clazz}", "/index/{sex}/{rare}/{clazz}/{character}/{png}"})
    public String index(@PathVariable("sex") String sex, @PathVariable("rare") String rare, @PathVariable("clazz") String clazz, @PathVariable(name = "character", required = false) String character, @PathVariable(name = "png", required = false) String png, Model model) {
        return pageService.index(sex, rare, clazz, character, png, model);
    }

    @GetMapping("/show")
    public String png(@RequestParam(required = false, defaultValue = "new") String status, Model model) {
        return pageService.show(status, model);
    }

    @GetMapping("/update")
    public String update() {
        return "update";
    }
}
