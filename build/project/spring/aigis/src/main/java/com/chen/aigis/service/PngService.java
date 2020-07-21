package com.chen.aigis.service;

import com.chen.aigis.dao.PngDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PngService {
    @Autowired
    private PngDao pngDao;

    public ResponseEntity<?> getByCharacter(String character) {
        return ResponseEntity.ok(Map.of("data", pngDao.getByCharacter(Map.of("character", character))));
    }
}
