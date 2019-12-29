package com.gerrnbutton.entity.espi;

import com.gerrnbutton.entity.Authorization;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "usage_point")
public class UsagePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "energy_data_id")
    private int id;

    @Column(name = "title")
    private String title;

    @OneToOne
    @JoinColumn(name = "interval_block_id")
    private IntervalBlock intervalBlock;

    @OneToOne
    @JoinColumn(name="reading_type_id")
    private ReadingType readingType;

    @Column(name = "updated")
    private String 	updated;

    @ManyToOne
    @JoinColumn(name="authorization_id")
    private Authorization authorization;
}
