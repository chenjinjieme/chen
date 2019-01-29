package com.chen.aigis.dao;

import com.chen.aigis.bean.Character;

import java.util.List;
import java.util.Map;

public interface PngDao {
    List<Map<String, Object>> getByCharacter(Map<String, Object> map);

    List<Character> getAll();

    int add(Map<String, Object> map);
}
