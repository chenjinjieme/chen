package com.chen.spring.aigis.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PNGDao {
    @Select("""
            SELECT u.name
                 , u.url
                 , p.count
            FROM png p
                     INNER JOIN url u ON p.url = u.id
                     LEFT JOIN unit c ON p.unit = c.id
            WHERE c.unit = #{unit}""")
    List<Map<String, Object>> getByUnit(Map<String, Object> map);

    @Select("""
            SELECT p.unit id
                 , c.unit
                 , c.own
                 , u.name
                 , u.url
                 , p.count
            FROM png p
                     LEFT JOIN url u ON p.url = u.id
                     LEFT JOIN unit c ON p.unit = c.id
            ORDER BY p.unit, p.type, p."index\"""")
    @ResultMap("com.chen.spring.aigis.mapper.unit")
    List<Map<String, Object>> getAll();

    @Insert("""
            INSERT INTO png(unit, type, "index", url)
            SELECT #{character}
                 , #{type}
                 , #{index}
                 , max(id)
            FROM url""")
    void add(Map<String, Object> map);
}
