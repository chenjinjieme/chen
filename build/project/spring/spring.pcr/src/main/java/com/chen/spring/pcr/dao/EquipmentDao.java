package com.chen.spring.pcr.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface EquipmentDao {
    @Select("""
            SELECT id
                 , equipment
            FROM equipment
            WHERE equipment = #{equipment}""")
    Map<String, Object> get(Map<String, Object> map);

    @Select("""
            SELECT e.id
                 , e.equipment
                 , e.rare
                 , e.rank
                 , e.type
                 , e.item
                 , e.own
                 , c."order"
                 , m.id mid
                 , c.material
                 , c.num
            FROM equipment e
                     LEFT JOIN compose c ON e.equipment = c.equipment
                     LEFT JOIN equipment m ON c.material = m.equipment
            ORDER BY e.rare, e.rank, e.type, e.item, e.id""")
    @ResultMap("com.chen.spring.pcr.mapper.equipment")
    List<Map<String, Object>> getAll(Map<String, Object> map);

    @Update("""
            UPDATE equipment
            SET own = #{own}
            WHERE equipment = #{equipment}""")
    void update(Map<String, Object> map);

    @Update("""
            UPDATE equipment
            SET own = own - 1
            WHERE equipment = #{equipment}""")
    void set(Map<String, Object> map);
}
