package com.chen.spring.pcr.dao;

import java.util.List;
import java.util.Map;

public interface UnitDao {
    Map<String, Object> get(Map<String, Object> map);

    List<Map<String, Object>> getAll(Map<String, Object> map);
}
