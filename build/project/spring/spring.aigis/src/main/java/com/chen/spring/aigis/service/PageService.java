package com.chen.spring.aigis.service;

import com.chen.spring.aigis.dao.ClassDao;
import com.chen.spring.aigis.dao.PNGDao;
import com.chen.spring.aigis.dao.RareDao;
import com.chen.spring.aigis.dao.UnitDao;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;

@Service
public class PageService {
    private final ClassDao classDao;
    private final PNGDao pngDao;
    private final RareDao rareDao;
    private final UnitDao unitDao;
    private static final String HOST = "http://assets.millennium-war.net/";

    public PageService(ClassDao classDao, PNGDao pngDao, RareDao rareDao, UnitDao unitDao) {
        this.classDao = classDao;
        this.pngDao = pngDao;
        this.rareDao = rareDao;
        this.unitDao = unitDao;
    }

    public String index(String sex, String rare, String clazz, String unit, String png, Model model) {
        model.addAttribute("rareList", rareDao.getAll()).addAttribute("classList", classDao.getAll());
        if (unit != null) {
            var pngList = pngDao.getByUnit(Map.of("unit", unit));
            var img = "";
            for (var u : pngList)
                if (png.equals(u.get("name").toString())) {
                    img = HOST + u.get("url");
                    break;
                }
            model.addAttribute("unitList", unitDao.get(Map.of("sex", sex, "rare", rare, "class", clazz))).addAttribute("pngList", pngList).addAttribute("img", img);
        }
        return "aigis";
    }

    public String show(String status, Model model) {
        model.addAttribute("rareList", rareDao.getAll()).addAttribute("classList", classDao.getAll()).addAttribute("unitList", pngDao.getAll()).addAttribute("status", status);
        return "show";
    }

    public String update() {
        return "update";
    }
}
