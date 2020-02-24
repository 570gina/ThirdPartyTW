package com.gerrnbutton.service;

import com.gerrnbutton.entity.espi.IntervalReading;
import com.gerrnbutton.entity.espi.UsagePoint;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class DataProcessingService {

    private Gson g = new Gson();

    public String organizeDataByEachMonth(String data){
        UsagePoint usagePoint = g.fromJson(data, UsagePoint.class);
        int previousYear = 0;
        ArrayList newData = new ArrayList();
        newData.add(usagePoint.getUpdate());
        newData.add(usagePoint.getReadingType().getPowerOfTenMultiplier());
        int[] valueAndCost = new int[25];
        for (IntervalReading i : usagePoint.getList()){
            if(i.getYear() == previousYear){
                valueAndCost[i.getMonth()] += i.getValue();
                valueAndCost[i.getMonth()+12] += i.getCost();
            }else{
                if(previousYear != 0)
                    newData.add(valueAndCost);
                previousYear = i.getYear();
                valueAndCost = new int[25];
                valueAndCost[0] = previousYear;
                valueAndCost[i.getMonth()] = i.getValue();
                valueAndCost[i.getMonth()+12] = i.getCost();
            }
        }
        newData.add(valueAndCost);
        return g.toJson(newData);
    }
    public String organizeDataByEachDay(String data, String year, String month){
        UsagePoint usagePoint = g.fromJson(data, UsagePoint.class);
        ArrayList<IntervalReading> tempData = new ArrayList<IntervalReading>();
        int previousDay = 0;
        int maxDay = 0;
        for (IntervalReading i : usagePoint.getList()){
            if(i.getYear() == Integer.parseInt(year) && i.getMonth() == Integer.parseInt(month)){
                tempData.add(i);
                maxDay = i.getDay();
            }
        }
        int[] newData = new int[maxDay * 2 + 1];
        for (IntervalReading i : tempData){
            newData[i.getDay()] += i.getValue();
            newData[i.getDay() + maxDay] += i.getCost();
        }
        return g.toJson(newData);
    }
    public String organizeDataByEachHour(String data, String year, String month, String day){
        UsagePoint usagePoint = g.fromJson(data, UsagePoint.class);
        int[] newData = new int[48];
        for (IntervalReading i : usagePoint.getList()){
            if(i.getYear() == Integer.parseInt(year) && i.getMonth() == Integer.parseInt(month) && i.getDay() == Integer.parseInt(day)){
                newData[i.getHour()] = i.getValue();
                newData[i.getHour()+24] = i.getCost();
            }
        }
        return g.toJson(newData);
    }
}
