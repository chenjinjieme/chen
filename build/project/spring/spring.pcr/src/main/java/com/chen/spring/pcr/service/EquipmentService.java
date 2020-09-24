package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

@Service
public class EquipmentService {
    private final EquipmentDao equipmentDao;

    public EquipmentService(EquipmentDao equipmentDao) {
        this.equipmentDao = equipmentDao;
    }

    public synchronized ResponseEntity<?> update(String equipment, String own) {
        equipmentDao.update(Map.of("equipment", equipment, "own", own));
        return ResponseEntity.ok("");
    }

    public synchronized ResponseEntity<?> icon(String equipment) {
        return ResponseEntity.status(301).location(URI.create("../../icon/装備/icon_equipment_" + equipmentDao.get(Map.of("equipment", equipment)).get("id") + ".png")).build();
    }
}
