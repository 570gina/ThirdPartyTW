package com.gerrnbutton.entity.espi;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class UsagePoint {
    private ArrayList<IntervalReading> list = new ArrayList<IntervalReading>();
    private ReadingType readingType;
    private Date update;

    public UsagePoint(){
        update = new Date();
    }
    public void addIntervalReading(IntervalReading intervalReading){
        list.add(intervalReading);
    }
}
