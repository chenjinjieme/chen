package com.chen.spring.pcr.bean;

import lombok.Data;

import java.util.List;

@Data
public class Equipment {
    private int id;
    private String equipment;
    private int rare;
    private int rank;
    private int type;
    private int item;
    private int own;
    private int left;
    private List<Material> materialList;

    @Data
    public static class Material {
        private int order;
        private String material;
        private int num;
    }
}
