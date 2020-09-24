package com.chen.spring.aigis.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface URLDao {
    @Select("""
            SELECT name
                 , url
            FROM url""")
    List<Map<String, Object>> getAll();

    @Insert("""
            INSERT INTO url(name, url)
            VALUES (#{name}, #{url})""")
    void add(Map<String, Object> map);

    @Update("""
            UPDATE url
            SET url = #{url}
            WHERE name = #{name}""")
    void update(Map<String, Object> map);
}
