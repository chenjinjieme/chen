package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import com.chen.spring.pcr.dao.UnitDao;
import com.chen.spring.pcr.dao.UnitEquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UnitEquipmentService {
    @Autowired
    private UnitEquipmentDao unitEquipmentDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private EquipmentDao equipmentDao;

    public void addAll(String unit, String rank) {
        var id = ((Number) unitDao.get(Map.of("unit", unit)).get("id")).intValue() * 1000 + Integer.parseInt(rank) * 10;
        for (var order = 1; order <= 6; order++) unitEquipmentDao.add(Map.of("id", id + order, "unit", unit, "rank", rank, "order", order, "equipment", "？？？", "set", 0));
    }

    public void update(String unit, String rank, String order, String equipment) {
        unitEquipmentDao.update(Map.of("unit", unit, "rank", rank, "order", order, "equipment", equipment, "set", -6));
    }

    public void set(String unit, String rank, String order) {
        Map<String, Object> map = Map.of("unit", unit, "rank", rank, "order", order);
        unitEquipmentDao.set(map);
        equipmentDao.set(Map.of("equipment", unitEquipmentDao.get(map).get("equipment")));
    }
}
