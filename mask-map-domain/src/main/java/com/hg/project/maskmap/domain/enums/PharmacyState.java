package com.hg.project.maskmap.domain.enums;


import org.springframework.util.StringUtils;

public enum PharmacyState {
    PLENTY,
    SOME,
    FEW,
    EMPTY,

    SOLDOUT,
    SALE,
    READY
    ;

    public static PharmacyState getType(String remain_stat) {
        if (StringUtils.isEmpty(remain_stat)) {
            return EMPTY;
        }
        try {
            return PharmacyState.valueOf(remain_stat.toUpperCase());
        } catch (Exception e) {
            return EMPTY;
        }
    }
}