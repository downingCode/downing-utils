package com.downing.util.entity;


import lombok.Data;

@Data
public class SensitiveWord {

    private Integer id;
    private String wordId;
    private String wordType;
    private String word;
}
