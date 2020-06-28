package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentDao equipmentDao;

    public void update(String equipment, String own) {
        equipmentDao.update(Map.of("equipment", equipment, "own", own));
    }
}
