package ru.koshkin.PerfTestHelper.Mappers;

import org.mapstruct.Mapper;
import ru.koshkin.PerfTestHelper.DTO.ScenarioDTO;
import ru.koshkin.PerfTestHelper.Entities.Scenario;

@Mapper(componentModel = "spring")
public interface ScenarioMapper {

    ScenarioDTO toDTO(Scenario scenario);

    Scenario toEntity(ScenarioDTO scenarioDTO);
}
