package com.chen.aigis.service;

import com.chen.aigis.dao.CharacterDao;
import com.chen.aigis.dao.ClassDao;
import com.chen.aigis.dao.PngDao;
import com.chen.aigis.dao.RareDao;
import com.chen.core.base.data.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
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
        Parameter parameter = new Parameter("sex", sex).add("rare", rare).add("class", clazz);
        if (character != null) {
            List<Map<String, Object>> pngs = pngDao.getByCharacter(parameter.add("character", character));
            String img = "";
            for (Map<String, Object> u : pngs)
                if (png.equals(u.get("name").toString())) {
                    img = HOST + u.get("url");
                    break;
                }
            parameter.add("characters", characterDao.get(parameter)).add("pngs", pngs).add("png", png).add("img", img);
        }
        model.addAllAttributes(parameter.add("rares", rareDao.getAll()).add("classes", classDao.getAll()));
        return "aigis";
    }

    public String show(String status, Model model) {
        model.addAllAttributes(new Parameter("rares", rareDao.getAll()).add("classes", classDao.getAll()).add("characters", pngDao.getAll()).add("status", status));
        return "show";
    }
}
