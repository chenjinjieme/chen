package com.chen.spring.aigis.service;

import com.chen.spring.aigis.dao.UnitDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnitService {
    private final UnitDao unitDao;

    public UnitService(UnitDao unitDao) {
        this.unitDao = unitDao;
    }

    public ResponseEntity<?> get(String sex, String rare, String clazz) {
        return ResponseEntity.ok(unitDao.get(Map.of("sex", sex, "rare", rare, "class", clazz)));
    }

    public ResponseEntity<?> add(String id, String name, String sex, String rare, String clazz) {
        unitDao.add(Map.of("id", id, "name", name, "sex", sex, "rare", rare, "class", clazz));
        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> delete(String id) {
        unitDao.delete(Map.of("id", id));
        return ResponseEntity.ok("");
    }
}
