package com.gerrnbutton.entity.espi;

import lombok.Data;

@Data
public class IntervalDay {
    private int id;
    private int[] data;
    private int year;
    private int month;
    private int day;
    private int week;
    private int cost;
    public IntervalDay(){
        cost = 0;
        data = new int[24];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }
    public void addCost(int cost){
        this.cost += cost;
    }
    public void addData(int value, int index){
        data[index] += value;
    }

}
