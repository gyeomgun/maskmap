package com.hg.project.maskmap.domain.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mask", url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1")
public interface MaskDataClient {
    @GetMapping(value = "/storesByGeo/json", produces = "application/json", consumes = "application/json")
    String getStore(@RequestParam("lat") Double lat,
                                    @RequestParam("lng") Double lng,
                                    @RequestParam("m") Integer distance);


}
