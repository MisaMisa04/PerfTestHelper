package ru.koshkin.PerfTestHelper.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.koshkin.PerfTestHelper.DTO.SuccessDto;
import ru.koshkin.PerfTestHelper.enums.CalcMethod;
import ru.koshkin.PerfTestHelper.DTO.OperationDTO;
import ru.koshkin.PerfTestHelper.DTO.OperationDataDTO;
import ru.koshkin.PerfTestHelper.Entities.Operation;
import ru.koshkin.PerfTestHelper.Entities.Scenario;
import ru.koshkin.PerfTestHelper.Mappers.OperationDataMapper;
import ru.koshkin.PerfTestHelper.Mappers.OperationMapper;
import ru.koshkin.PerfTestHelper.repositories.OperationRepo;
import ru.koshkin.PerfTestHelper.repositories.ScenarioRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.koshkin.PerfTestHelper.services.ScenarioService.checkScenarioBelongsToUser;

@Service
public class OperationService {

    @Autowired
    private ScenarioRepo scenarioRepo;

    @Autowired
    private OperationRepo operationRepo;

    @Autowired
    private OperationMapper operationMapper;

    @Autowired
    private OperationDataMapper operationDataMapper;


    public static void checkOperationBelongsToUser(Operation operation, Long userId) throws Exception {
        if (!operation.getScenario().getUser().getId().equals(userId)) {
            throw new Exception("This operation belongs to other user");
        }
    }

    public List<OperationDTO> getScenarioOperations(Long scenarioId, Long userId) throws Exception {
        final Scenario scenario = scenarioRepo.findById(scenarioId).orElseThrow();
        checkScenarioBelongsToUser(scenario, userId);
        final List<Operation> operations = scenario.getOperations();
        return operations.stream()
                .map(operationMapper::toDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public OperationDataDTO getOperationData(Long scenarioId, Long operationId, Long userId) throws Exception {
        Operation operation = operationRepo.findByIdAndScenarioId(operationId, scenarioId).orElseThrow();
        checkOperationBelongsToUser(operation, userId);
        return operationDataMapper.toDTO(operation);
    }

    public OperationDataDTO calculateAndStoreOperation(OperationDataDTO operationDataDTO, Long userId) throws Exception {
        Operation operation = operationRepo.findById(operationDataDTO.getOperationId()).orElseThrow();
        checkOperationBelongsToUser(operation, userId);
        final CalcMethod calculateMethod = CalcMethod.fromOrdinal(operationDataDTO.getCalculateMethod());
        final Boolean isDistributed = Optional.ofNullable(operationDataDTO.getIsDistributed()).orElse(false);
        final int gensAmount = isDistributed ? operationDataDTO.getGensAmount() : 1;
        final Double sla = operationDataDTO.getSLA();
        final Integer rps = operationDataDTO.getRps();
        Double ctt = operationDataDTO.getCTT();
        Integer threadsAmount = operationDataDTO.getThreadsAmount();
        switch (calculateMethod) {
            case AUTO: {
                double pacing = sla * 1.2; // условно + 20% запаса
                ctt = (double) 60 / pacing;
                // no break here: defining best CTT from SLA and calculating like CTT-based
            }
            case CTT: {
                threadsAmount = (int) Math.floor((rps * 60) / (gensAmount * ctt));
                // re-calculating CTT since threads wil be floored and won't be accurate
                // as now we go to thread-based calc, so no break here as well
            }
            case THREADS: {
                ctt = (double) (rps * 60) / (double) (gensAmount * threadsAmount);
                // savin our entity (if exists)
                try {
                    operation.setCalculateMethod(calculateMethod);
                    operation.setIsDistributed(isDistributed);
                    operation.setGensAmount(gensAmount);
                    operation.setSLA(sla);
                    operation.setRps(rps);
                    operation.setCTT(ctt);
                    operation.setThreadsAmount(threadsAmount);
                    operationRepo.save(operation);
                } finally {
                    // anyway filling our DTO with recalculated data
                    operationDataDTO.setCTT(ctt);
                    operationDataDTO.setThreadsAmount(threadsAmount);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Poshel hanui!");
            }
        }
        return operationDataDTO;
    }

    public OperationDTO postNewOperation(Long scenarioId, String operationName, Long userId) throws Exception {
        Operation operation = new Operation();
        Scenario scenario = scenarioRepo.findById(scenarioId).orElseThrow();
        checkScenarioBelongsToUser(scenario, userId);
        operation.setName(operationName);
        operation.setScenario(scenario);
        operation = operationRepo.save(operation);
        return operationMapper.toDTO(operation);
    }

    public OperationDTO renameOperation(OperationDTO operationDTO, Long userId) throws Exception {
        Operation operation = operationRepo.findById(operationDTO.getId()).orElseThrow();
        checkOperationBelongsToUser(operation, userId);
        operation.setName(operationDTO.getName());
        operation = operationRepo.save(operation);
        return operationMapper.toDTO(operation);
    }

    public SuccessDto deleteOperation(Long operationId, Long userId) {
        try {
            Operation operation = operationRepo.findById(operationId).orElseThrow();
            checkOperationBelongsToUser(operation, userId);
            operationRepo.delete(operation);
            return new SuccessDto(true);
        } catch (Exception e) {
            return new SuccessDto(false, "This operation belongs to other user");
        }
    }
}
