package ru.koshkin.PerfTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.koshkin.PerfTestHelper.DTO.OperationDTO;
import ru.koshkin.PerfTestHelper.DTO.OperationDataDTO;
import ru.koshkin.PerfTestHelper.MockSecurityContext.WithMockSecurityContext;
import ru.koshkin.PerfTestHelper.Security.JwtAuthenticationFilter;
import ru.koshkin.PerfTestHelper.controllers.OperationController;
import ru.koshkin.PerfTestHelper.enums.CalcMethod;
import ru.koshkin.PerfTestHelper.services.OperationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockSecurityContext
    public void getOperations_ReturnsList_WhenScenarioIdProvided() throws Exception {
        final Long scenarioId = 1L;
        final String operationName = "Operation Sample";
        List<OperationDTO> expected = List.of(new OperationDTO(scenarioId, operationName));

        when(operationService.getScenarioOperations(anyLong(), anyLong())).thenReturn(expected);

        // Act & Assert: имитация GET‑запроса и проверка ответа
        mockMvc.perform(get("/api/v1/operations") // имитируем GET /operations
                        .param("scenarioId", scenarioId.toString())) // с параметром scenarioId=1
                .andExpect(status().isOk()) // ожидаем HTTP 200 OK
                .andExpect(content().contentType("application/json")) // Content-Type: application/json
                .andExpect(jsonPath("$.length()").value(1)) // в ответе 1 элемент
                .andExpect(jsonPath("$[0].id").value(scenarioId)) // у элемента id=1
                .andExpect(jsonPath("$[0].name").value(operationName));
    }

    @Test
    @WithMockSecurityContext
    public void getOperationData_returnOperationDataDTO_WhenScenarioIdAndOperationIdProvided() throws Exception {
        final Long scenarioId = 1L;
        final Long operationId = 3L;
        final String operationName="Test Operation";
        final OperationDataDTO expected = new OperationDataDTO();
        expected.setScenarioId(scenarioId);
        expected.setOperationId(operationId);
        expected.setCTT(60d);
        expected.setThreadsAmount(1);
        expected.setRps(60);
        expected.setCalculateMethod(0);
        expected.setIsDistributed(false);
        expected.setSLA(0.9);
        expected.setOperationName(operationName);
        when(operationService.getOperationData(anyLong(), anyLong(), anyLong())).thenReturn(expected);

        mockMvc.perform(
                        get("/api/v1/operation")
                                .param("scenarioId", scenarioId.toString())
                                .param("operationId", operationId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.scenarioId").value(scenarioId))
                .andExpect(jsonPath("$.operationId").value(operationId))
                .andExpect(jsonPath("$.operationName").value(operationName))
                .andExpect(jsonPath("$.ctt").value(60))
                .andExpect(jsonPath("$.threadsAmount").value(1))
                .andExpect(jsonPath("$.rps").value(60))
                .andExpect(jsonPath("$.calculateMethod").value(0))
                .andExpect(jsonPath("$.isDistributed").value(false))
                .andExpect(jsonPath("$.sla").value(0.9));
    }
}
