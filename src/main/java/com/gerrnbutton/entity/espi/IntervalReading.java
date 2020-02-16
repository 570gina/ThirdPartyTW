package com.gerrnbutton.entity.espi;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
public class IntervalReading {
    int cost;
    int value;
    Date date;
    public void addCostAndValue(int cost, int value){
        this.cost += cost;
        this.value += value;
    }

    public String dateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        return sdf.format(date);
    }

    public int getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.parseInt(sdf.format(date));
    }

    public int getMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return Integer.parseInt(sdf.format(date));
    }

    public int getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return Integer.parseInt(sdf.format(date));
    }
}
