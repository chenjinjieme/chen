package com.chen.spring.pcr.service;

import com.chen.spring.pcr.bean.Equipment;
import com.chen.spring.pcr.dao.EquipmentDao;
import com.chen.spring.pcr.dao.UnitDao;
import com.chen.spring.pcr.dao.UnitEquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PageService {
    @Autowired
    private EquipmentDao equipmentDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private UnitEquipmentDao unitEquipmentDao;

    public String equipment(String sort, String hide, Model model) {
        var equipmentList = equipmentDao.getAll(Map.of());
        var equipmentMap = equipmentList.stream().collect(Collectors.toMap(Equipment::getEquipment, equipment -> equipment));
        unitEquipmentDao.getUnset(Map.of()).forEach(map -> {
            var equipment = equipmentMap.get(map.get("equipment").toString());
            equipment.setLeft(equipment.getLeft() - ((Number) map.get("num")).intValue());
        });
        for (var i = equipmentList.size() - 1; i >= 0; i--) {
            var equipment = equipmentList.get(i);
            var materialList = equipment.getMaterialList();
            if (materialList.size() > 0) {
                var left = equipment.getLeft();
                if (left < 0) {
                    equipment.setLeft(0);
                    materialList.forEach(material -> {
                        var order = equipmentMap.get(material.getMaterial());
                        order.setLeft(order.getLeft() + material.getNum() * left);
                    });
                }
            }
        }
        if ("1".equals(sort)) equipmentList.sort(Comparator.comparingInt(Equipment::getType).thenComparing(Equipment::getRare).thenComparing(Equipment::getItem).thenComparing(Equipment::getId));
        model.addAttribute("equipmentList", equipmentList).addAttribute("hide", "1".equals(hide));
        return "装備";
    }

    public String unit(Model model) {
        model.addAttribute("unitList", unitDao.getAll(Map.of()));
        return "キャラ";
    }

    public String unitEquipment(String unit, Model model) {
        model.addAttribute("rankList", unitEquipmentDao.getAll(Map.of("unit", unit))).addAttribute("unit", unit);
        return "キャラ装備";
    }
}
