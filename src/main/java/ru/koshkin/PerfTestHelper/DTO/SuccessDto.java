package ru.koshkin.PerfTestHelper.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessDto {
    private boolean success;
    private String commentary;

    public SuccessDto(boolean success) {
        this.success = success;
    }
}
