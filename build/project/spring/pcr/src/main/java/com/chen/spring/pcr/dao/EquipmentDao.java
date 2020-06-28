package com.chen.spring.pcr.dao;

import com.chen.spring.pcr.bean.Equipment;

import java.util.List;
import java.util.Map;

public interface EquipmentDao {
    Map<String, Object> get(Map<String, Object> map);

    List<Equipment> getAll(Map<String, Object> map);

    void update(Map<String, Object> map);

    void set(Map<String, Object> map);
}
