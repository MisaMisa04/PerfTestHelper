package ru.koshkin.PerfTestHelper.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.koshkin.PerfTestHelper.Entities.Operation;

import java.util.Optional;

public interface OperationRepo extends CrudRepository<Operation, Long> {

    Optional<Operation> findByIdAndScenarioId(Long operationId, Long scenarioId);
}
