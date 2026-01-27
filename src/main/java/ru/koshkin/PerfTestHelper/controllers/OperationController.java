package ru.koshkin.PerfTestHelper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.koshkin.PerfTestHelper.DTO.OperationDTO;
import ru.koshkin.PerfTestHelper.DTO.OperationDataDTO;
import ru.koshkin.PerfTestHelper.DTO.SuccessDto;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.services.OperationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @RequestMapping(method = RequestMethod.GET, path = "operations")
    public List<OperationDTO> getOperations(@RequestParam("scenarioId") Long scenarioId) throws Exception {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.getScenarioOperations(scenarioId, userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "operation")
    public OperationDataDTO getOperationData(@RequestParam("scenarioId") Long scenarioId,
                                             @RequestParam("operationId") Long operationId) throws Exception {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.getOperationData(scenarioId, operationId, userId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "operation")
    public OperationDataDTO postOperationData(@RequestBody OperationDataDTO operationDataDTO) throws Exception {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.calculateAndStoreOperation(operationDataDTO, userId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "operations/new")
    public OperationDTO postNewOperation(@RequestParam("newOperationName") String newOperationName,
                                         @RequestParam("scenarioId") Long scenarioId) throws Exception {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.postNewOperation(scenarioId, newOperationName, userId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "rename_operation")
    public OperationDTO postRenameOperation(@RequestBody OperationDTO operationDTO) throws Exception {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.renameOperation(operationDTO, userId);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "operation")
    public SuccessDto deleteOperation(@RequestParam("operationId") Long operationId) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return operationService.deleteOperation(operationId, userId);
    }

}
