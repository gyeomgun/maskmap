package com.hg.project.maskmap.domain.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hg.project.maskmap.domain.dto.dataportal.Item;
import com.hg.project.maskmap.domain.enums.PharmacyState;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Document("pharmacy")
@Data
@Builder
public class Pharmacy {
    @Id
    @Field("_id")
    private String id;
    @Field("name")
    private String name;
    @Field("addr")
    private String addr;
    @Field("map_img")
    private String mapImg;
    @Field("info")
    private String info;
    @Field("tel")
    private String tel;
    @Field("oper_time")
    private Map<String, String> operationTime;
    @Field("hpid")
    private String hpid;
    @Field("pos")
    private GeoJsonPoint position;
    @Field("updated_at")
    private Date updatedAt;
    @Field("hist")
    private List<StateHistory> stateHistory;
    @Field("state")
    private PharmacyState state;

    private String stockAtString;
    public String getUpdatedAtString() {
        if (updatedAt == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(updatedAt);
    }
    public List<String> getOperInfo() {
        if (CollectionUtils.isEmpty(operationTime)) {
            return Lists.newArrayList();
        }

        return operationTime.entrySet()
                .stream()
                .map(s -> s.getKey() + " : " + s.getValue())
                .collect(Collectors.toList());
    }

    public static Map<String, String> makeOperationTime(Item item) {
        Map<String, String> result = Maps.newHashMap();
        if (!StringUtils.isEmpty(item.getDutyTime1s())
                && !StringUtils.isEmpty(item.getDutyTime1c())) {
            result.put("월요일", item.getDutyTime1s() + "~" + item.getDutyTime1c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime2s())
                && !StringUtils.isEmpty(item.getDutyTime2c())) {
            result.put("화요일", item.getDutyTime2s() + "~" + item.getDutyTime2c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime3s())
                && !StringUtils.isEmpty(item.getDutyTime3c())) {
            result.put("수요일", item.getDutyTime3s() + "~" + item.getDutyTime3c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime4s())
                && !StringUtils.isEmpty(item.getDutyTime4c())) {
            result.put("목요일", item.getDutyTime4s() + "~" + item.getDutyTime4c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime5s())
                && !StringUtils.isEmpty(item.getDutyTime5c())) {
            result.put("금요일", item.getDutyTime5s() + "~" + item.getDutyTime5c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime6s())
                && !StringUtils.isEmpty(item.getDutyTime6c())) {
            result.put("토요일", item.getDutyTime6s() + "~" + item.getDutyTime6c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime7s())
                && !StringUtils.isEmpty(item.getDutyTime7c())) {
            result.put("일요일", item.getDutyTime7s() + "~" + item.getDutyTime7c());
        }

        if (!StringUtils.isEmpty(item.getDutyTime8s())
                && !StringUtils.isEmpty(item.getDutyTime8c())) {
            result.put("공휴일", item.getDutyTime8s() + "~" + item.getDutyTime8c());
        }

        return result;
    }
}
