package ru.koshkin.PerfTestHelper.Mappers;

import org.mapstruct.Mapper;
import ru.koshkin.PerfTestHelper.DTO.OperationDTO;
import ru.koshkin.PerfTestHelper.Entities.Operation;

@Mapper(componentModel = "spring")  //
public interface OperationMapper {

    OperationDTO toDTO(Operation operation);

    Operation toEntity(OperationDTO operationDTO);
}
