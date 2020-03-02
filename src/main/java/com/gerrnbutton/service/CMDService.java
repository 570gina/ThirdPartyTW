package com.gerrnbutton.service;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.espi.*;
import com.google.gson.Gson;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.*;

@Service
public class CMDService {

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    public String getEnergyData(Authorization authorization, int update) {
        if (redisTemplate.opsForValue().get(authorization.getNumber()) == null || update == 1)
            updateData(authorization);
         return redisTemplate.opsForValue().get(authorization.getNumber())+"";
    }

    public void updateData(Authorization authorization) {
        Gson gson = new Gson();
        String url = "http://140.121.196.23:6020/RetailCustomer/" + authorization.getNumber() + "/DownloadMyData/UsagePoint";
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        //HttpHeaders headers = new HttpHeaders();
      //  headers.set("Authorization", authorization.getTokenType() + " " + authorization.getAccessToken());
        //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        redisTemplate.opsForValue().set(authorization.getNumber(), gson.toJson(parseXML(response.getBody())));
    }

    private UsagePoint parseXML(String xml){
        UsagePoint usagePoint = new UsagePoint();
        ReadingType readingType = new ReadingType();
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(xml));
            Element root = doc.getRootElement();
            List<Element> entryList = root.elements("entry");
            for(Element entry : entryList) {
                Element con = entry.element("content");
                if(con.element("IntervalBlock") != null){
                    parseIntervalData(usagePoint, con);
                }else if(con.element("ReadingType") != null) {
                    readingType.setPowerOfTenMultiplier(Integer.parseInt(con.element("ReadingType").element("powerOfTenMultiplier").getText()));
                    usagePoint.setReadingType(readingType);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return usagePoint;
    }

    public void parseIntervalData(UsagePoint usagePoint, Element con){
        List<Element> dataList = con.element("IntervalBlock").elements("IntervalReading");
        ArrayList<IntervalReading> tempList = new ArrayList<IntervalReading>();
        int previousValue;
        String previousDate = "";
        for(Element data : dataList) {
            IntervalReading intervalReading = new IntervalReading();
            intervalReading.setCost(Integer.parseInt(data.element("cost").getText()));
            intervalReading.setValue(Integer.parseInt(data.element("value").getText()));
            intervalReading.setDate(convertToDate(data.element("timePeriod").element("start").getText()));
            tempList.add(intervalReading);
        }
        Collections.sort(tempList, new Comparator<IntervalReading>() {
            @Override public int compare(IntervalReading d1, IntervalReading d2) {
                return d1.getDate().compareTo(d2.getDate()); // Ascending
            }
        });
        IntervalReading integration = new IntervalReading();
        previousValue = tempList.get(0).getValue();
        for (IntervalReading i : tempList){
            if(i.dateToString().equals(previousDate)) {
                integration.addCostAndValue(i.getCost(),i.getValue()-previousValue);
            }else{
                if(previousDate.length() > 0)
                    usagePoint.addIntervalReading(integration);
                previousDate = i.dateToString();
                integration = new IntervalReading();
                integration.setCost(i.getCost());
                integration.setValue(i.getValue()-previousValue);
                integration.setDate(i.getDate());
            }
            previousValue =  i.getValue();
        }
        usagePoint.addIntervalReading(integration);
    }

    public Date convertToDate(String timeStamp) {
       return new Date(Long.parseLong(timeStamp)*1000);
    }
}
