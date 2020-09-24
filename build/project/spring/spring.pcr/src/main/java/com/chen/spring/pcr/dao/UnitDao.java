package com.chen.spring.pcr.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UnitDao {
    @Select("""
            SELECT id
            FROM unit
            WHERE unit = #{unit}""")
    Map<String, Object> get(Map<String, Object> map);

    @Select("""
            SELECT id
                 , unit
                 , icon
                 , rank
                 , status
            FROM unit
            ORDER BY level DESC, star DESC, rank DESC, id DESC""")
    List<Map<String, Object>> getAll(Map<String, Object> map);
}
