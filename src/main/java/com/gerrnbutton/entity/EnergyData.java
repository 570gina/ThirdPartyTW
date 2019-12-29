package com.gerrnbutton.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "energy_data")
public class EnergyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "energy_data_id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "read_type")
    private String 	readType;

    @Column(name = "interval_data")
    private String	intervalData;

    @Column(name = "updated")
    private String 	updated;

    @OneToOne
    @JoinColumn(name="authorization_id")
    private Authorization authorization;
}
