package com.chen.spring.aigis.service;

import com.chen.spring.aigis.dao.PNGDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PNGService {
    private final PNGDao pngDao;

    public PNGService(PNGDao pngDao) {
        this.pngDao = pngDao;
    }

    public ResponseEntity<?> getByUnit(String unit) {
        return ResponseEntity.ok(pngDao.getByUnit(Map.of("unit", unit)));
    }
}
