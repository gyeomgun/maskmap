package com.hg.project.maskmap.web.controller;

import com.hg.project.maskmap.domain.entity.Pharmacy;
import com.hg.project.maskmap.domain.entity.StateHistory;
import com.hg.project.maskmap.domain.enums.PharmacyState;
import com.hg.project.maskmap.domain.service.MapService;
import com.hg.project.maskmap.web.dto.CommonResponse;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/map/v1")
@AllArgsConstructor
public class MapController {
    private static final Integer DEFAULT_MAX_DISTANCE = 600;

    private final MapService mapService;

    @GetMapping("/retrieve")
    public CommonResponse<List<Pharmacy>> retrieve(@RequestParam("lat") Double lat,
                                                   @RequestParam("lon") Double lon) throws IOException {
        try {
            List<Pharmacy> pharmacyList = mapService.findByGeoLocation(lon, lat, DEFAULT_MAX_DISTANCE);
            return CommonResponse.<List<Pharmacy>>builder()
                    .code("0000")
                    .data(pharmacyList)
                    .build();
        } catch (FeignException ex) {
            if (ex.status() == 429) {
                return CommonResponse.<List<Pharmacy>>builder()
                        .code("0429")
                        .message("서버에 요청량이 많아 조회에 실패했습니다. 다시 시도해주세요")
                        .build();
            }
            return CommonResponse.<List<Pharmacy>>builder()
                    .code("0999")
                    .message("네트워크 오류입니다.")
                    .build();
        } catch (Exception ex) {
            return CommonResponse.<List<Pharmacy>>builder()
                    .code("9999")
                    .message("오류")
                    .build();
        }
    }

    @PutMapping("/{id}/state/{state}")
    public void changeState(@PathVariable String id,
                            @PathVariable String state,
                            @RequestParam("lat") Double lat,
                            @RequestParam("lon") Double lon) {
        PharmacyState pharmacyState = PharmacyState.valueOf(state);
        mapService.setState(id, pharmacyState, lat, lon);
    }

    @GetMapping("/{id}/history")
    public CommonResponse<List<StateHistory>> getHistory(@PathVariable String id) {
        return CommonResponse.<List<StateHistory>>builder()
                .code("0000")
                .data(mapService.getHistory(id))
                .build();
    }

    @GetMapping("/autocomplete")
    public CommonResponse<List<Pharmacy>> autocomplete(@RequestParam("keyword") String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return CommonResponse.<List<Pharmacy>>builder().build();
        }
        return CommonResponse.<List<Pharmacy>>builder()
                .data(mapService.search(keyword, 10))
                .code("0000")
                .build();
    }
}
