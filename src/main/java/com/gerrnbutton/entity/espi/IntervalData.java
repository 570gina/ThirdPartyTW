package com.gerrnbutton.entity.espi;

import lombok.Data;
import java.util.ArrayList;

@Data
public class IntervalData {
    private int id;
    private ArrayList<IntervalDay> intervalDayList;

    public IntervalData(){
        intervalDayList = new ArrayList<IntervalDay>();
    }

}
