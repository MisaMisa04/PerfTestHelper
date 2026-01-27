package ru.koshkin.PerfTestHelper.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.koshkin.PerfTestHelper.Entities.Scenario;

public interface ScenarioRepo extends CrudRepository<Scenario, Long> {
}
