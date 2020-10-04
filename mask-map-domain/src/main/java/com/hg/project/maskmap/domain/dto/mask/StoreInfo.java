package com.hg.project.maskmap.domain.dto.mask;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@Document("temp_pharmacy")
public class StoreInfo {
    @Id
    @Field("_id")
    private String id;
    @Field("addr")
    private String addr;
    @Field("code")
    private String code;
    @Field("lat")
    private Double lat;
    @Field("lng")
    private Double lng;
    @Field("name")
    private String name;
    @Field("type")
    private String type;
    @Field("remain_stat")
    private String remain_stat;
    @Field("stock_at")
    private String stock_at;
    @Field("created_at")
    private String created_at;
    @Field("pos")
    private GeoJsonPoint position;

    public void makeId() {
        this.position = new GeoJsonPoint(lng,lat);
        id = name + addr.hashCode();
    }
}
