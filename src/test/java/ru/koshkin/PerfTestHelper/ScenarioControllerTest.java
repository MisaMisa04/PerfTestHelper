package ru.koshkin.PerfTestHelper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.koshkin.PerfTestHelper.DTO.ScenarioDTO;
import ru.koshkin.PerfTestHelper.MockSecurityContext.WithMockSecurityContext;
import ru.koshkin.PerfTestHelper.Security.JwtAuthenticationFilter;
import ru.koshkin.PerfTestHelper.controllers.ScenarioController;
import ru.koshkin.PerfTestHelper.services.ScenarioService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScenarioController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class ScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ScenarioService scenarioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @ParameterizedTest
    @ValueSource(strings = {"TestScenario", "AnotherScenario", "Scenario with spaces", "Special@#$%Characters"})
    @WithMockSecurityContext
    public void postNewScenario_returnNewScenarioDTO_whenNewScenarioNameProvided(String scenarioName) throws Exception {
        final ScenarioDTO expected = new ScenarioDTO();
        expected.setId(1L);
        expected.setName(scenarioName);
        when(scenarioService.postNewScenario(eq(scenarioName), anyLong())).thenReturn(expected);
        mockMvc.perform(
                        post("/api/v1/scenarios/new")
                                .param("newScenarioName", scenarioName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(scenarioName));

    }
}
