package com.chen.aigis.service;

import com.chen.aigis.dao.CharacterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.chen.spring.base.data.ControllerUtil.execute;
import static com.chen.spring.base.data.ControllerUtil.putData;

@Service
public class CharacterService {
    @Autowired
    private CharacterDao characterDao;

    public Map<String, Object> get(String sex, String rare, String clazz) {
        return putData(characterDao.get(Map.of("sex", sex, "rare", rare, "class", clazz)));
    }

    public Map<String, Object> add(String id, String name, String sex, String rare, String clazz) {
        return execute(characterDao.add(Map.of("id", id, "name", name, "sex", sex, "rare", rare, "class", clazz)));
    }

    public Map<String, Object> delete(String id) {
        return execute(characterDao.delete(Map.of("id", id)));
    }
}
