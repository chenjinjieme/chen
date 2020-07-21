package com.chen.aigis.service;

import com.chen.aigis.dao.CharacterDao;
import com.chen.aigis.dao.ClassDao;
import com.chen.aigis.dao.PngDao;
import com.chen.aigis.dao.RareDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;

@Service
public class PageService {
    @Autowired
    private RareDao rareDao;
    @Autowired
    private ClassDao classDao;
    @Autowired
    private CharacterDao characterDao;
    @Autowired
    private PngDao pngDao;
    private static final String HOST = "http://assets.millennium-war.net/";

    public String index(String sex, String rare, String clazz, String character, String png, Model model) {
        if (character != null) {
            var pngs = pngDao.getByCharacter(Map.of("character", character));
            var img = "";
            for (var u : pngs)
                if (png.equals(u.get("name").toString())) {
                    img = HOST + u.get("url");
                    break;
                }
            model.addAllAttributes(Map.of("rares", rareDao.getAll(), "classes", classDao.getAll(), "characters", characterDao.get(Map.of("sex", sex, "rare", rare, "class", clazz)), "pngs", pngs, "png", png, "img", img));
        } else model.addAllAttributes(Map.of("rares", rareDao.getAll(), "classes", classDao.getAll()));
        return "aigis";
    }

    public String show(String status, Model model) {
        model.addAllAttributes(Map.of("rares", rareDao.getAll(), "classes", classDao.getAll(), "characters", pngDao.getAll(), "status", status));
        return "show";
    }
}
