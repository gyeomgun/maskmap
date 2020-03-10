package com.hg.project.maskmap.domain.dto.dataportal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataPortalResponse {
    private DataPortalHeader header;
    private DataPortalBody body;
}
