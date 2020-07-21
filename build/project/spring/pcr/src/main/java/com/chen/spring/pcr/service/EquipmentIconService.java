package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

@Service
public class EquipmentIconService {
    @Autowired
    private EquipmentDao equipmentDao;

    public ResponseEntity icon(String equipment) {
        return ResponseEntity.status(301).location(URI.create("/プリコネR/icon/装備/icon_equipment_" + equipmentDao.get(Map.of("equipment", equipment)).get("id") + ".png")).build();
    }
}
