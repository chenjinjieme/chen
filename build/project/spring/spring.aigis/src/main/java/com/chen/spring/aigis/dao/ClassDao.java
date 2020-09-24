package com.chen.spring.aigis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassDao {
    @Select("""
            SELECT id
                 , class
                 , translate
            FROM class""")
    List<Map<String, Object>> getAll();
}
