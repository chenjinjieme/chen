package com.chen.spring.aigis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RareDao {
    @Select("""
            SELECT id
                 , rare
                 , translate
            FROM rare
            ORDER BY id DESC""")
    List<Map<String, Object>> getAll();
}
