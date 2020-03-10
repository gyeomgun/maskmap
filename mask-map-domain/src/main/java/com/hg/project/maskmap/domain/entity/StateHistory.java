package com.hg.project.maskmap.domain.entity;

import com.hg.project.maskmap.domain.enums.PharmacyState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class StateHistory {
    @Field("state")
    private PharmacyState state;
    @Field("updated_at")
    private Date updatedAt;

    public String getUpdatedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(updatedAt);
    }
}
