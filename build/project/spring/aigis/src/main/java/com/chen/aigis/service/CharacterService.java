package com.chen.aigis.service;

import com.chen.aigis.dao.CharacterDao;
import com.chen.base.data.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.chen.util.data.ControllerUtil.execute;
import static com.chen.util.data.ControllerUtil.putData;

@Service
public class CharacterService {
    @Autowired
    private CharacterDao characterDao;

    public Map<String, Object> get(String sex, String rare, String clazz) {
        return putData(characterDao.get(new Parameter("sex", sex).add("rare", rare).add("class", clazz)));
    }

    public Map<String, Object> add(String id, String name, String sex, String rare, String clazz) {
        return execute(characterDao.add(new Parameter("id", id).add("name", name).add("sex", sex).add("rare", rare).add("class", clazz)));
    }

    public Map<String, Object> delete(String id) {
        return execute(characterDao.delete(new Parameter("id", id)));
    }
}
