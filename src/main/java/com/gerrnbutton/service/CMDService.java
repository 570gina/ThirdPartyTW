package com.gerrnbutton.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.espi.IntervalBlock;
import com.gerrnbutton.entity.espi.IntervalDay;
import com.gerrnbutton.entity.espi.ReadingType;
import com.gerrnbutton.entity.espi.UsagePoint;
import com.gerrnbutton.repository.AuthorizationRepository;
import com.gerrnbutton.repository.espi.IntervalBlockRepository;
import com.gerrnbutton.repository.espi.IntervalDayRepository;
import com.gerrnbutton.repository.espi.ReadingTypeRepository;
import com.gerrnbutton.repository.espi.UsagePointRepository;
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
import java.util.Date;
import java.util.List;

@Service
public class CMDService {

    @Autowired
    private UsagePointRepository usagePointRepository;
    @Autowired
    private ReadingTypeRepository readingTypeRepository;
    @Autowired
    private IntervalBlockRepository intervalBlockRepository;
    @Autowired
    private IntervalDayRepository intervalDayRepository;

    private int lastValue = 0;

    public UsagePoint getUsagePoints(Authorization authorization) {
        UsagePoint usagePoint = usagePointRepository.findByAuthorization(authorization);
        if (usagePoint == null) {
            updateData(authorization);
            usagePoint = usagePointRepository.findByAuthorization(authorization);
        }
        return usagePoint;
    }

    private void updateData(Authorization authorization) {
        String url = "http://140.121.196.23:6020/RetailCustomer/" + authorization.getNumber() + "/DownloadMyData/UsagePoint";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization.getTokenType() + " " + authorization.getAccessToken());
        HttpEntity<String> requestEntity = new HttpEntity<String>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        parseXML(response.getBody());
    }

    private void parseXML(String xml){
        UsagePoint usagePoint = new UsagePoint();
        IntervalBlock intervalBlock = new IntervalBlock();
        ReadingType readingType = new ReadingType();

        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(xml));
            Element root = doc.getRootElement();
            List<Element> entryList = root.elements("entry");
            for(Element entry : entryList) {
                Element con = entry.element("content");
                if(con.element("MeterReading") != null) {
                    usagePoint = new UsagePoint();
                    intervalBlock = new IntervalBlock();
                    readingType = new ReadingType();
                    parseUsagePoint(usagePoint, entry);
                }else if(con.element("IntervalBlock") != null){
                    parseIntervalBlock(intervalBlock, con);

                }else if(con.element("ReadingType") != null) {
                    parseReadingType(readingType, con);
                }
            }
            usagePoint.setIntervalBlock(intervalBlockRepository.save(intervalBlock));
            usagePoint.setReadingType(readingTypeRepository.save(readingType));
            usagePointRepository.save(usagePoint);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void parseUsagePoint(UsagePoint usagePoint, Element entry){
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Date date = new Date();
        usagePoint.setTitle(entry.element("title").getText());
        usagePoint.setUpdated(dateFormat.format(date));
    }

    public void parseReadingType(ReadingType readingType, Element con){
        readingType.setPowerOfTenMultiplier(Integer.parseInt(con.element("ReadingType").element("powerOfTenMultiplier").getText()));
    }

    public void parseIntervalBlock(IntervalBlock intervalBlock, Element con){
        List<Element> dataList = con.element("IntervalBlock").elements("IntervalReading");
        IntervalDay intervalDay = new IntervalDay();
        for(Element data : dataList) {
            int cost = Integer.parseInt(data.element("cost").getText());
            String time[] = convertUnixTimestamp(data.element("timePeriod").element("start").getText());
            int value;
            if (lastValue == 0) {
                value = Integer.parseInt(data.element("value").getText());
                lastValue = value;
            }else{
                value = Integer.parseInt(data.element("value").getText()) - lastValue;
            }
            intervalDay = intervalDayRepository.findByDate(time[0], time[1], time[2]);
             if (intervalDay == null){
                intervalDay = new IntervalDay();
                intervalDay.setYear(time[0]);
                intervalDay.setMonth(time[1]);
                intervalDay.setDay(time[2]);
                intervalDay.setCost(cost);
                int[] intArray = getNewDayArray();
                intArray[Integer.parseInt(time[3])]+=value;
                intervalDay.setData(intArrayToString(intArray));
                intervalDay = intervalDayRepository.save(intervalDay);
            }else{
                intervalDay.setCost(intervalDay.getCost()+cost);
                String [] strArray = intervalDay.getData().split(" ");
                int [] intArray = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }
                intArray[Integer.parseInt(time[3])]+=value;
                intervalDay.setData(intArrayToString(intArray));
                intervalDay = intervalDayRepository.save(intervalDay);
            }
        }
    }

    public String intArrayToString(int[] intArray){
        String str = intArray[0] + "";
        for (int i = 1; i < intArray.length; i++) {
            str += " " + intArray[i];
        }
        return str;
    }

    public int[] getNewDayArray(){
        int[] arr = new int[24];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
        return arr;
    }

    public String[] convertUnixTimestamp(String timeStamp) {
        Date time=new Date(Long.parseLong(timeStamp)*1000);
        SimpleDateFormat y = new SimpleDateFormat("yyyy");
        SimpleDateFormat m = new SimpleDateFormat("MM");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        SimpleDateFormat h = new SimpleDateFormat("HH");
        String[] s = {y.format(time), m.format(time), d.format(time), h.format(time)};
        return s;
    }
}
