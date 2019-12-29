package com.gerrnbutton.entity.espi;

import lombok.Data;

@Data
public class UsagePoint {

    private int id;
    private String title;
    private IntervalData intervalData;
    private ReadingType readingType;
    private String 	updated;
}
