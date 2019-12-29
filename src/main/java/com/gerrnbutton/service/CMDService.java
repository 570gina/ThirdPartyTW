package com.gerrnbutton.service;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.EnergyData;
import com.gerrnbutton.entity.espi.*;
import com.gerrnbutton.repository.EnergyDataRepository;
import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CMDService {
    @Autowired
    private EnergyDataRepository energyDataRepository;

    public String getEnergyData(Authorization authorization, int update) {
        Gson gson = new Gson();
        EnergyData energyData = energyDataRepository.findByAuthorization(authorization);
        if (energyData == null) {
            updateData(authorization);
            energyData = energyDataRepository.findByAuthorization(authorization);
        }else if(update == 1){
            energyData.setAuthorization(null);
            energyDataRepository.save(energyData);
            updateData(authorization);
            energyData = energyDataRepository.findByAuthorization(authorization);
        }
        energyData.setAuthorization(null);
        return gson.toJson(energyData);
    }

    public void updateData(Authorization authorization) {
        String url = "http://140.121.196.23:6020/RetailCustomer/" + authorization.getNumber() + "/DownloadMyData/UsagePoint/1";
        System.out.println(url);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization.getTokenType() + " " + authorization.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        saveToSQL(parseXML(response.getBody()), authorization);
    }
    public void saveToSQL(UsagePoint usagePoint, Authorization authorization){
        Gson gson = new Gson();
        EnergyData energyData = new EnergyData();
        energyData.setTitle(usagePoint.getTitle());
        energyData.setIntervalData(gson.toJson(usagePoint.getIntervalData()));
        energyData.setReadType(gson.toJson(usagePoint.getReadingType()));
        energyData.setUpdated(usagePoint.getUpdated());
        energyData.setAuthorization(authorization);
        energyDataRepository.save(energyData);
    }

    private UsagePoint parseXML(String xml){
        UsagePoint usagePoint = new UsagePoint();
        IntervalData intervalData = new IntervalData();
        ReadingType readingType = new ReadingType();
        ArrayList<IntervalReading> intervalReadings = new ArrayList();

        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(xml));
            Element root = doc.getRootElement();
            List<Element> entryList = root.elements("entry");
            for(Element entry : entryList) {
                Element con = entry.element("content");
                if(con.element("MeterReading") != null) {
                    parseUsagePoint(usagePoint, entry);
                }else if(con.element("IntervalBlock") != null){
                    parseIntervalData(intervalReadings, con);
                }else if(con.element("ReadingType") != null) {
                    parseReadingType(readingType, con);
                    usagePoint.setReadingType(readingType);
                }
            }
            Collections.sort(intervalReadings, new Comparator<IntervalReading>() {
                @Override public int compare(IntervalReading p1, IntervalReading p2) {
                    return p1.getId() - p2.getId(); // Ascending
                }
            });
            setIntervalData(intervalData, intervalReadings);
            usagePoint.setIntervalData(intervalData);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return usagePoint;
    }

    public void parseUsagePoint(UsagePoint usagePoint, Element entry){
        String strDateFormat = "yyyy-MM-dd HH:mm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Date date = new Date();
        usagePoint.setTitle(entry.element("title").getText());
        usagePoint.setUpdated(dateFormat.format(date));
    }

    public void parseReadingType(ReadingType readingType, Element con){
        readingType.setPowerOfTenMultiplier(Integer.parseInt(con.element("ReadingType").element("powerOfTenMultiplier").getText()));
    }

    public void parseIntervalData(ArrayList<IntervalReading> intervalReadings, Element con){
        List<Element> dataList = con.element("IntervalBlock").elements("IntervalReading");
        int j = 0;
        for(Element data : dataList) {
            IntervalReading intervalReading = new IntervalReading();
            intervalReading.setCost(Integer.parseInt(data.element("cost").getText()));
            intervalReading.setValue(Integer.parseInt(data.element("value").getText()));
            intervalReading.setId(Integer.parseInt(data.element("id").getText()));
            intervalReading.setStart(data.element("timePeriod").element("start").getText());
            intervalReadings.add(intervalReading);
        }
    }

    public void setIntervalData(IntervalData intervalData, ArrayList<IntervalReading> intervalReadings){
        IntervalDay intervalDay = new IntervalDay();
        int lastValue = 0;
        int value;
        int time[];

        for (IntervalReading ir : intervalReadings) {
            time = convertUnixTimestamp(ir.getStart());
            if (lastValue == 0){
                value = 0;
            }else {
                value = ir.getValue()-lastValue;
            }
            lastValue = ir.getValue();
            if (intervalDay.getYear() != time[0] || intervalDay.getMonth()!= time[1] || intervalDay.getDay() != time[2]) {
                intervalData.getIntervalDayList().add(intervalDay);
                intervalDay = new IntervalDay();
                intervalDay.setYear(time[0]);
                intervalDay.setMonth(time[1]);
                intervalDay.setDay(time[2]);
                intervalDay.setWeek(time[4]);
            }

            intervalDay.addCost(ir.getCost());
            intervalDay.addData(value, time[3]);
        }
        intervalData.getIntervalDayList().add(intervalDay);

    }

    public int[] convertUnixTimestamp(String timeStamp) {
        Date time=new Date(Long.parseLong(timeStamp)*1000);
        SimpleDateFormat y = new SimpleDateFormat("yyyy");
        SimpleDateFormat m = new SimpleDateFormat("MM");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat h = new SimpleDateFormat("HH");
        SimpleDateFormat u = new SimpleDateFormat("u");
        int[] s = {Integer.parseInt(y.format(time)), Integer.parseInt(m.format(time)), Integer.parseInt(d.format(time)), Integer.parseInt(h.format(time)), Integer.parseInt(u.format(time))};
        return s;
    }
}
