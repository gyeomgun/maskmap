package com.hg.project.maskmap.domain.client;

import com.hg.project.maskmap.domain.dto.dataportal.DataPortalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "data.portal", url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService")
interface DataPortalClient {
    @GetMapping(value = "/getParmacyFullDown")
    DataPortalResponse getEntirePharmarcy(@RequestParam("ServiceKey") String serviceKey,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam("numOfRows") Integer numOfRows);
}
