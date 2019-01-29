package com.chen.aigis.dao;

import java.util.List;
import java.util.Map;

public interface URLDao {
    List<Map<String, Object>> getAll();

    int add(Map<String, Object> map);

    int update(Map<String, Object> map);
}
