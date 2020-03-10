package com.hg.project.maskmap.web.controller;

import com.hg.project.maskmap.domain.entity.Pharmacy;
import com.hg.project.maskmap.domain.entity.StateHistory;
import com.hg.project.maskmap.domain.enums.PharmacyState;
import com.hg.project.maskmap.domain.service.MapService;
import com.hg.project.maskmap.web.dto.CommonResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/map/v1")
@AllArgsConstructor
public class MapController {
    private static final Integer DEFAULT_MAX_DISTANCE = 1_000;

    private final MapService mapService;

    @GetMapping("/retrieve")
    public CommonResponse<List<Pharmacy>> retrieve(@RequestParam("lat") Double lat,
                                                   @RequestParam("lon") Double lon) throws IOException {
        List<Pharmacy> pharmacyList = mapService.findByGeoLocation(lon, lat, DEFAULT_MAX_DISTANCE);
        return CommonResponse.<List<Pharmacy>>builder()
                .code("0000")
                .data(pharmacyList)
                .build();
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
}
