package com.hg.project.maskmap.domain.dto.mask;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StoreInfo {
    private String addr;
    private String code;
    private Double lat;
    private Double lng;
    private String name;
    private String type;
    private String remain_stat;
    private String stock_at;
    private String created_at;
}
