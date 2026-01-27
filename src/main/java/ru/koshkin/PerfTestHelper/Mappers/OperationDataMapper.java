package ru.koshkin.PerfTestHelper.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.koshkin.PerfTestHelper.DTO.OperationDataDTO;
import ru.koshkin.PerfTestHelper.Entities.Operation;

@Mapper(componentModel = "spring")
public interface OperationDataMapper {

    @Mapping(target = "scenarioId", source = "scenario.id")
    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "operationName", source = "name")
    @Mapping(target = "calculateMethod", expression = "java(operation.getCalculateMethod().ordinal())")
    OperationDataDTO toDTO(Operation operation);

    Operation toEntity(OperationDataDTO operationDataDTO);
}
