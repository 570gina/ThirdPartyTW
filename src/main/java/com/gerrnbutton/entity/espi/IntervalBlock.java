package com.gerrnbutton.entity.espi;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "interval_block")
public class IntervalBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_block_id")
    private int id;

    @OneToMany(mappedBy = "intervalBlock", cascade = CascadeType.ALL)
    private List<IntervalDay> intervalDayList;
}
