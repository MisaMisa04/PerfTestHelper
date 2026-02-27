package ru.koshkin.PerfTestHelper.services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.koshkin.PerfTestHelper.DTO.OperationDataDTO;
import ru.koshkin.PerfTestHelper.DTO.ScenarioDTO;
import ru.koshkin.PerfTestHelper.DTO.SuccessDto;
import ru.koshkin.PerfTestHelper.Entities.Operation;
import ru.koshkin.PerfTestHelper.Entities.Scenario;
import ru.koshkin.PerfTestHelper.Mappers.OperationDataMapper;
import ru.koshkin.PerfTestHelper.Mappers.ScenarioMapper;
import ru.koshkin.PerfTestHelper.repositories.ScenarioRepo;
import ru.koshkin.PerfTestHelper.repositories.UserRepo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ScenarioService {

    @Autowired
    private ScenarioRepo scenarioRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ScenarioMapper scenarioMapper;

    @Autowired
    private OperationDataMapper operationDataMapper;

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

    public String formJSONOfScenario(Long scenarioId, Long userId) throws Exception {
            final Scenario scenario = scenarioRepo.findById(scenarioId).orElseThrow();
            checkScenarioBelongsToUser(scenario, userId);
            final List<Operation> operations = scenario.getOperations();
            final int size = operations.size();
            if (size == 0)
                throw new Exception("No operations found, JSON cannot be formed");
            CountDownLatch cdl = new CountDownLatch(size);
            OperationDataDTO[] dtos = new OperationDataDTO[size];
            try (var threadPool = Executors.newFixedThreadPool(size)) {
                for (var i = 0; i < size; i++) {
                    final var idx = i;
                    threadPool.execute(() -> {
                        try {
                            final Operation curOperation = operations.get(idx);
                            dtos[idx] = operationDataMapper.toDTO(curOperation);
                            dtos[idx].setScenarioId(null);
                        } finally {
                            cdl.countDown();
                        }
                    });
                }
            }
/*         ok, I know this could be made much easier with Stream API
           But I have already used Stream API is "getUserScenarios" method
           Now I wanted to "touch" ThreadPool and CountDownLatch
           However, this is how it could be made with Stream API:
           List dtos = scenario.getOperations().stream()
               .map(operationDataMapper::toDTO)
               .collect(Collectors.toCollection(ArrayList::new));
 */
            HashMap<String, Object> result = new HashMap<>();
            result.put("scenarioId", scenario.getId());
            result.put("scenarioName", scenario.getName());
            cdl.await();
            result.put("operations", dtos);
            return new Gson().toJson(result);



    }

}
