package com.gerrnbutton.entity.espi;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reading_type")
public class ReadingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_type_id")
    private int id;

    @Column(name = "power_of_ten_multiplier")
    private int powerOfTenMultiplier;


}
