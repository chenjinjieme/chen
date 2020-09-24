package com.chen.spring.pcr.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UnitEquipmentDao {
    @Select("""
            SELECT equipment
            FROM unit_equipment
            WHERE unit = #{unit}
              AND rank = #{rank}
              AND "order" = #{order}""")
    Map<String, Object> get(Map<String, Object> map);

    @Select("""
            SELECT ue.rank
                 , ue."order"
                 , e.id
                 , ue.equipment
                 , ue."set"
            FROM unit_equipment ue
                     LEFT JOIN equipment e ON ue.equipment = e.equipment
            WHERE unit = #{unit}""")
    @ResultMap("com.chen.spring.pcr.mapper.rank")
    List<Map<String, Object>> getAll(Map<String, Object> map);

    @Select("""
            SELECT equipment
                 , count(equipment) num
            FROM unit_equipment
            WHERE "set" = -6
            GROUP BY equipment""")
    List<Map<String, Object>> getUnset(Map<String, Object> map);

    @Insert("""
            INSERT INTO unit_equipment(id, unit, rank, "order", equipment, "set")
            VALUES (#{id}, #{unit}, #{rank}, #{order}, #{equipment}, #{set})""")
    void add(Map<String, Object> map);

    @Update("""
            UPDATE unit_equipment
            SET equipment = #{equipment},
                "set"     = #{set}
            WHERE unit = #{unit}
              AND rank = #{rank}
              AND "order" = #{order}""")
    void update(Map<String, Object> map);

    @Update("""
            UPDATE unit_equipment
            SET "set" = 1
            WHERE unit = #{unit}
              AND rank = #{rank}
              AND "order" = #{order}""")
    void set(Map<String, Object> map);
}
