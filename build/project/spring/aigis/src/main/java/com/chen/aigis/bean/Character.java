package com.chen.aigis.bean;

import lombok.Data;

import java.util.List;

@Data
public class Character {
    private String id;
    private String character;
    private String own;
    private List<Png> pngs;

    @Data
    public static class Png {
        private String name;
        private String url;
        private String count;
    }
}
