package com.chen.aigis.service;

import com.chen.aigis.dao.PngDao;
import com.chen.base.data.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.chen.spring.base.data.ControllerUtil.putData;

@Service
public class PngService {
    @Autowired
    private PngDao pngDao;

    public Map<String, Object> getByCharacter(String character) {
        return putData(pngDao.getByCharacter(new Parameter("character", character)));
    }
}
