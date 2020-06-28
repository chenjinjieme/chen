package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EquipmentIconService {
    @Autowired
    private EquipmentDao equipmentDao;

    public String icon(String equipment) {
        return "forward:/icon/装備/icon_equipment_" + equipmentDao.get(Map.of("equipment", equipment)).get("id") + ".png";
    }
}
