package com.hg.project.maskmap.domain.dto.dataportal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataPortalBody {
    private Items items;
    private Long numOfRows;
    private Long pageNo;
    private Long totalCount;
}
