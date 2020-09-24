package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import com.chen.spring.pcr.dao.UnitDao;
import com.chen.spring.pcr.dao.UnitEquipmentDao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnitEquipmentService {
    private final EquipmentDao equipmentDao;
    private final UnitDao unitDao;
    private final UnitEquipmentDao unitEquipmentDao;

    public UnitEquipmentService(EquipmentDao equipmentDao, UnitDao unitDao, UnitEquipmentDao unitEquipmentDao) {
        this.equipmentDao = equipmentDao;
        this.unitDao = unitDao;
        this.unitEquipmentDao = unitEquipmentDao;
    }

    public synchronized ResponseEntity<?> addAll(String unit, String rank) {
        var id = ((Number) unitDao.get(Map.of("unit", unit)).get("id")).intValue() * 1000 + Integer.parseInt(rank) * 10;
        for (var order = 1; order <= 6; order++) unitEquipmentDao.add(Map.of("id", id + order, "unit", unit, "rank", rank, "order", order, "equipment", "？？？", "set", 0));
        return ResponseEntity.ok("");
    }

    public synchronized ResponseEntity<?> update(String unit, String rank, String order, String equipment) {
        unitEquipmentDao.update(Map.of("unit", unit, "rank", rank, "order", order, "equipment", equipment, "set", -6));
        return ResponseEntity.ok("");
    }

    public synchronized ResponseEntity<?> set(String unit, String rank, String order) {
        Map<String, Object> map = Map.of("unit", unit, "rank", rank, "order", order);
        unitEquipmentDao.set(map);
        equipmentDao.set(Map.of("equipment", unitEquipmentDao.get(map).get("equipment")));
        return ResponseEntity.ok("");
    }
}
