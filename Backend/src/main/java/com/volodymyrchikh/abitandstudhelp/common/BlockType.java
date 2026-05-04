package com.volodymyrchikh.abitandstudhelp.common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum BlockType {

    HUMANITARIAN("Гуманітарний блок"),
    SCIENTIFIC("Природничо-науковий блок"),
    PROFESSIONAL("Фаховий блок"),
    PDFC("Фахова дисципліна вільного вибору");

    private final String displayName;

    BlockType(String displayName) {
        this.displayName = displayName;
    }
}
