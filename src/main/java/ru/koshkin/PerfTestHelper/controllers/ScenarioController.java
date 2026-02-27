package ru.koshkin.PerfTestHelper.controllers;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.koshkin.PerfTestHelper.DTO.ScenarioDTO;
import ru.koshkin.PerfTestHelper.DTO.SuccessDto;
import ru.koshkin.PerfTestHelper.Entities.User;
import ru.koshkin.PerfTestHelper.services.ScenarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;

    @RequestMapping(method = RequestMethod.GET, path = "scenarios")
    public List<ScenarioDTO> getScenarios() {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return scenarioService.getUserScenarios(userId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "scenarios/new")
    public ScenarioDTO postNewScenario(@RequestParam("newScenarioName") String newScenarioName) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return scenarioService.postNewScenario(newScenarioName, userId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "rename_scenario")
    public SuccessDto postRenameScenario(@RequestBody ScenarioDTO scenarioDTO) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return scenarioService.renameScenario(scenarioDTO, userId);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "scenario")
    public SuccessDto deleteScenario(@RequestParam("scenarioId") Long scenarioId) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return scenarioService.deleteScenarion(scenarioId, userId);

    }

    @RequestMapping(method = RequestMethod.GET, path = "scenario_json")
    public ResponseEntity<String> formJSONOfScenario(@RequestParam("scenarioId") Long scenarioId) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        try {
            String bodyJSON = scenarioService.formJSONOfScenario(scenarioId, userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "scenario-" + scenarioId + ".json");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bodyJSON);
        } catch (Exception e) {
            var err = new SuccessDto(false, e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(new Gson().toJson(err));
        }

    }
}
