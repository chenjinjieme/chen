package com.chen.spring.pcr.bean;

import lombok.Data;

import java.util.List;

@Data
public class Rank {
    private int rank;
    private List<Equipment> equipmentList;

    @Data
    public static class Equipment {
        private int order;
        private String equipment;
        private int set;
    }
}
