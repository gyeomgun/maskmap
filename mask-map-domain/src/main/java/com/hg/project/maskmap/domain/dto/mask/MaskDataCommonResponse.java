package com.hg.project.maskmap.domain.dto.mask;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MaskDataCommonResponse {
    private Long count;
    private List<StoreInfo> stores;
}
