package com.chen.aigis.dao;

import java.util.List;
import java.util.Map;

public interface CharacterDao {
    List<Map<String, Object>> get(Map<String, Object> map);

    int add(Map<String, Object> map);

    int delete(Map<String, Object> map);
}
