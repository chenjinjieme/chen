package com.chen.aigis.service;

import com.chen.aigis.dao.CharacterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CharacterService {
    @Autowired
    private CharacterDao characterDao;

    public ResponseEntity<?> get(String sex, String rare, String clazz) {
        return ResponseEntity.ok(Map.of("data", characterDao.get(Map.of("sex", sex, "rare", rare, "class", clazz))));
    }

    public ResponseEntity<?> add(String id, String name, String sex, String rare, String clazz) {
        characterDao.add(Map.of("id", id, "name", name, "sex", sex, "rare", rare, "class", clazz));
        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> delete(String id) {
        characterDao.delete(Map.of("id", id));
        return ResponseEntity.ok("");
    }
}
