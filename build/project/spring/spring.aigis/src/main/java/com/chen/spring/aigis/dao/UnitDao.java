package com.chen.spring.aigis.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UnitDao {
    @Select("""
            SELECT u.unit
                 , u.own
            FROM unit u
                     LEFT JOIN sex s ON u.sex = s.id
                     LEFT JOIN rare r ON u.rare = r.id
                     LEFT JOIN class c on u.class = c.id
            WHERE s.sex = #{sex}
              AND r.rare = #{rare}
              AND c.class = #{class}""")
    List<Map<String, Object>> get(Map<String, Object> map);

    @Insert("""
            INSERT INTO unit(id, unit, sex, rare, class)
            VALUES (#{id}, #{name}, #{sex}, #{rare}, #{class})""")
    void add(Map<String, Object> map);

    @Delete("""
            DELETE
            FROM unit
            WHERE id = #{id}""")
    void delete(Map<String, Object> map);
}
