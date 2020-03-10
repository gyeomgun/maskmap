package com.hg.project.maskmap.domain.service;

import com.hg.project.maskmap.domain.dto.dataportal.DataPortalResponse;
import com.hg.project.maskmap.domain.dto.dataportal.Item;
import com.hg.project.maskmap.domain.entity.Pharmacy;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataPortalService {
    public DataPortalResponse getEntirePharmarcy(@RequestParam("ServiceKey") String serviceKey,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam("numOfRows") Integer numOfRows) throws Exception {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo.toString(), "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows.toString(), "UTF-8")); /*한 페이지 결과 수*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        System.out.println(sb.toString());
        conn.disconnect();

        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        org.w3c.dom.Element varElement = document.getDocumentElement();
        JAXBContext context = JAXBContext.newInstance(DataPortalResponse.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<DataPortalResponse> loader = unmarshaller.unmarshal(varElement, DataPortalResponse.class);
        return loader.getValue();
    }

    public List<Pharmacy> convert(List<Item> items) {
        return items.stream()
                .filter(s -> s != null && s.getWgs84Lat() != null && s.getWgs84Lon() != null)
                .map(s -> Pharmacy.builder()
                        .id(s.getRnum().toString())
                        .name(s.getDutyName())
                        .addr(s.getDutyAddr())
                        .mapImg(s.getDutyMapimg())
                        .info(s.getDutyInf())
                        .tel(s.getDutyTel1())
                        .hpid(s.getHpid())
                        .updatedAt(new Date())
                        .position(new GeoJsonPoint(s.getWgs84Lon(), s.getWgs84Lat()))
                        .operationTime(Pharmacy.makeOperationTime(s))
                        .build())
                .collect(Collectors.toList());
    }
}
