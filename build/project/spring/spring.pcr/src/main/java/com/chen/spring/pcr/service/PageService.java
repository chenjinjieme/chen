package com.chen.spring.pcr.service;

import com.chen.spring.pcr.dao.EquipmentDao;
import com.chen.spring.pcr.dao.UnitDao;
import com.chen.spring.pcr.dao.UnitEquipmentDao;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PageService {
    private final EquipmentDao equipmentDao;
    private final UnitDao unitDao;
    private final UnitEquipmentDao unitEquipmentDao;

    public PageService(EquipmentDao equipmentDao, UnitDao unitDao, UnitEquipmentDao unitEquipmentDao) {
        this.equipmentDao = equipmentDao;
        this.unitDao = unitDao;
        this.unitEquipmentDao = unitEquipmentDao;
    }

    public String equipment(String sort, String hide, Model model) {
        var equipmentList = equipmentDao.getAll(Map.of());
        var equipmentMap = equipmentList.stream().collect(Collectors.toMap(equipment -> equipment.get("equipment"), equipment -> equipment));
        unitEquipmentDao.getUnset(Map.of()).forEach(map -> {
            var equipment = equipmentMap.get(map.get("equipment").toString());
            equipment.put("left", (Integer) equipment.get("left") - (Integer) map.get("num"));
        });
        for (var i = equipmentList.size() - 1; i >= 0; i--) {
            var equipment = equipmentList.get(i);
            @SuppressWarnings("unchecked")
            var materialList = (List<Map<String, Object>>) equipment.get("materialList");
            if (materialList.size() > 0) {
                var left = (Integer) equipment.get("left");
                if (left < 0) {
                    equipment.put("left", 0);
                    materialList.forEach(material -> {
                        var order = equipmentMap.get(material.get("material"));
                        order.put("left", (Integer) order.get("left") + (Integer) material.get("num") * left);
                    });
                }
            }
        }
        if ("1".equals(sort)) equipmentList.sort(Comparator.<Map<String, Object>>comparingInt(equipment -> (int) equipment.get("type")).thenComparing(equipment -> (int) equipment.get("rare")).thenComparing(equipment -> (int) equipment.get("item")).thenComparing(equipment -> (int) equipment.get("id")));
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
