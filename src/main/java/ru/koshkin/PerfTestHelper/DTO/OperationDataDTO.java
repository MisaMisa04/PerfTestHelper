package ru.koshkin.PerfTestHelper.DTO;

import lombok.Getter;
import lombok.Setter;
import ru.koshkin.PerfTestHelper.enums.CalcMethod;

@Getter
@Setter
public class OperationDataDTO {

    private Long scenarioId;
    private Long operationId;
    private String operationName;
    private Integer rps;
    private Double SLA;
    private Double CTT;
    private Integer threadsAmount;
    private Integer calculateMethod;
    private Boolean isDistributed;
    private Integer gensAmount;
}
