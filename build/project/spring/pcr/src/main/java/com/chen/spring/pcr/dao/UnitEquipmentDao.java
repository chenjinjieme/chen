package com.chen.spring.pcr.dao;

import com.chen.spring.pcr.bean.Rank;

import java.util.List;
import java.util.Map;

public interface UnitEquipmentDao {
    Map<String, Object> get(Map<String, Object> map);

    List<Rank> getAll(Map<String, Object> map);

    List<Map<String, Object>> getUnset(Map<String, Object> map);

    void add(Map<String, Object> map);

    void update(Map<String, Object> map);

    void set(Map<String, Object> map);
}
