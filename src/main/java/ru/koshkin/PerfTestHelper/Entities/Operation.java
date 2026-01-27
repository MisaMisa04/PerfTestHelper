package ru.koshkin.PerfTestHelper.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.koshkin.PerfTestHelper.enums.CalcMethod;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer rps;

    @Column
    private Double SLA;

    @Column
    private Double CTT;

    @Column
    private Integer threadsAmount;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    @Enumerated(EnumType.ORDINAL)
    private CalcMethod calculateMethod = CalcMethod.AUTO;

    @Column
    private Boolean isDistributed;

    @Column
    private Integer gensAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

}
