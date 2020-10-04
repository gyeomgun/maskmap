package com.hg.project.maskmap.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.hg.project.maskmap.domain.client.MaskDataClient;
import com.hg.project.maskmap.domain.dto.mask.MaskDataCommonResponse;
import com.hg.project.maskmap.domain.dto.mask.StoreInfo;
import com.hg.project.maskmap.domain.entity.Pharmacy;
import com.hg.project.maskmap.domain.entity.StateHistory;
import com.hg.project.maskmap.domain.enums.PharmacyState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MapService {
    private final MongoTemplate mongoTemplate;
    private final MaskDataClient maskDataClient;

    public List<Pharmacy> findByGeoLocation(Double lon, Double lat, Integer maxDistance) throws IOException {
        MaskDataCommonResponse response = getMaskDataCommonResponse(lon, lat, maxDistance);
        Query query = new Query();
        query.addCriteria(Criteria.where("pos").nearSphere(new Point(lon, lat)).maxDistance(maxDistance));
        query.limit(50);
        List<Pharmacy> pharmacies = mongoTemplate.find(query, Pharmacy.class);


        Map<String, Pharmacy> pharmacyMap = pharmacies.stream().collect(Collectors.toMap(s -> s.getName(), s -> s, (s1, s2) -> s1));

        return response.getStores().stream()
                .map(s -> convert(s, pharmacyMap))
                .collect(Collectors.toList());

    }

    private MaskDataCommonResponse getMaskDataCommonResponse(Double lon, Double lat, Integer maxDistance) throws IOException {
        try {
            String store = maskDataClient.getStore(lat, lon, maxDistance);
            ObjectMapper objectMapper = new ObjectMapper();
            MaskDataCommonResponse response = objectMapper.readValue(store, MaskDataCommonResponse.class);

            response.getStores().stream().peek(s -> s.makeId()).forEach(s -> mongoTemplate.save(s));

            return response;
        } catch (Exception ex) {
            Query query = new Query();
            query.addCriteria(Criteria.where("pos").nearSphere(new Point(lon, lat)).maxDistance(maxDistance));
            query.limit(50);
            List<StoreInfo> pharmacies = mongoTemplate.find(query, StoreInfo.class);
            MaskDataCommonResponse response = new MaskDataCommonResponse();
            response.setStores(pharmacies);
            response.setCount(-1L);
            return response;
        }

    }

    private Pharmacy convert(StoreInfo s, Map<String, Pharmacy> map) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date updatedAt = null;
        try {
            updatedAt = sdf.parse(s.getCreated_at());
        } catch (Exception ex) {

        }

        Pharmacy.PharmacyBuilder state = Pharmacy.builder()
                .position(new GeoJsonPoint(s.getLng(), s.getLat()))
                .updatedAt(updatedAt)
                .stockAtString(s.getStock_at())
                .name(s.getName())
                .addr(s.getAddr())
                .state(PharmacyState.getType(s.getRemain_stat()));

        if (map.containsKey(s.getName())) {
            Pharmacy pharmacy = map.get(s.getName());
            state.tel(pharmacy.getTel())
                    .operationTime(pharmacy.getOperationTime())
                    .mapImg(pharmacy.getMapImg());


        }

        return state.build();
    }

    public void setState(String id, PharmacyState state, Double lat, Double lon) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Pharmacy one = mongoTemplate.findOne(query, Pharmacy.class);

        CheckDistance(one.getPosition(), new GeoJsonPoint(lon, lat));

        if (state == one.getState()) {
            return;
        }

        StateHistory stateHistory = new StateHistory(state, new Date());
        one.setState(state);
        if (null == one.getStateHistory()) {
            one.setStateHistory(Lists.newArrayList());
        }
        one.getStateHistory().add(stateHistory);

        one.setStateHistory(one.getStateHistory().stream()
                .sorted(Comparator.comparing(StateHistory::getUpdatedAt).reversed())
                .limit(30)
                .collect(Collectors.toList()));

        mongoTemplate.save(one);
    }

    private void CheckDistance(GeoJsonPoint pharmacyPos, GeoJsonPoint userPos) {

        Double distance = 110.25 * Math.sqrt(Math.pow(pharmacyPos.getY() - userPos.getY(), 2)
                + Math.pow((pharmacyPos.getX() - userPos.getX()) * Math.cos(Math.toRadians(userPos.getY())), 2)) * 1_000;

        Preconditions.checkState(distance < 100.0, "Distance over 100m");
    }

    public List<StateHistory> getHistory(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Pharmacy one = mongoTemplate.findOne(query, Pharmacy.class);
        return one.getStateHistory();
    }

    public List<Pharmacy> search(String keyword, int count) {
        Query query = new Query();
        Pattern pat = Pattern.compile(keyword);
        Criteria cr = (new Criteria()).orOperator(Criteria.where("addr").regex(pat),
                Criteria.where("name").regex(pat));
        query.addCriteria(cr);
        query.limit(count);
        List<Pharmacy> pharmacies = mongoTemplate.find(query, Pharmacy.class);
        return pharmacies;
    }
}
