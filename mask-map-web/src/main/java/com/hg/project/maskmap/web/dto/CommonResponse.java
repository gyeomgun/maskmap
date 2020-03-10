package com.hg.project.maskmap.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommonResponse<T> {
    private String code;
    private String message;
    private T data;
}
