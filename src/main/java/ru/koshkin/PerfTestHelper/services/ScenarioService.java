package ru.koshkin.PerfTestHelper.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.koshkin.PerfTestHelper.DTO.ScenarioDTO;
import ru.koshkin.PerfTestHelper.DTO.SuccessDto;
import ru.koshkin.PerfTestHelper.Entities.Scenario;
import ru.koshkin.PerfTestHelper.Mappers.ScenarioMapper;
import ru.koshkin.PerfTestHelper.repositories.ScenarioRepo;
import ru.koshkin.PerfTestHelper.repositories.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenarioService {

    @Autowired
    private ScenarioRepo scenarioRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ScenarioMapper scenarioMapper;

    public static void checkScenarioBelongsToUser(Scenario scenario, Long userId) throws Exception {
        if (!scenario.getUser().getId().equals(userId)) {
            throw new Exception("This scenario belongs to other user");
        }
    }

    public List<ScenarioDTO> getUserScenarios(Long userId) {
        final List<Scenario> scenarios = userRepo.findById(userId).orElseThrow().getScenarios();
        return scenarios.stream()
                .map(scenarioMapper::toDTO) // same as .map(scenario -> scenarioMapper.toDTO(scenario))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ScenarioDTO postNewScenario(String name, Long userId) {
        Scenario scenario = new Scenario();
        scenario.setName(name);
        scenario.setUser(userRepo.findById(userId).orElseThrow());
        scenario = scenarioRepo.save(scenario);
        return scenarioMapper.toDTO(scenario);
    }

    public SuccessDto renameScenario(ScenarioDTO scenarioDTO, Long userId) {
        Scenario scenario = scenarioRepo.findById(scenarioDTO.getId()).orElseThrow();
        try {
            checkScenarioBelongsToUser(scenario, userId);
        } catch (Exception e) {
            return new SuccessDto(false, e.getLocalizedMessage());
        }
        scenario.setName(scenarioDTO.getName());
        scenarioRepo.save(scenario);
        return new SuccessDto(true);
    }

    public SuccessDto deleteScenarion(Long scenarioId, Long userId) {
        try {
            final Scenario scenario = scenarioRepo.findById(scenarioId).orElseThrow();
            checkScenarioBelongsToUser(scenario, userId);
            scenarioRepo.delete(scenario);
            return new SuccessDto(true);
        } catch (Exception e) {
            return new SuccessDto(false, e.getLocalizedMessage());
        }
    }

}
