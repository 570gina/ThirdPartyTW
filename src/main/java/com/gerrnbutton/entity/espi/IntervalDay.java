package com.gerrnbutton.entity.espi;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@Entity
@Table(name = "interval_day")
public class IntervalDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_day_id")
    private int id;

    @ManyToOne
    @JoinColumn
    private IntervalBlock intervalBlock;

    @Column(name = "data")
    private String data;

    @Column(name = "year")
    private String year;

    @Column(name = "month")
    private String month;

    @Column(name = "day")
    private String day;

    @Column(name = "cost")
    private int cost;

}
