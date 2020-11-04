package de.bublitz.balancer.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.model.ConsumptionPoint;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@AutoConfigureMockMvc
public class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @AfterAll
    public void tearDown() throws Exception {
        mockMvc.perform(delete("/load/delete").param("name", "Test"));
    }

    @BeforeAll
    public void setUp() {
        mapper.findAndRegisterModules();
    }

    @Test
    public void addSinglePointTest() throws Exception {
        ConsumptionPoint consumptionPoint = new ConsumptionPoint("Test", 1);
        this.mockMvc.perform(post("/load/addPoint").content(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(consumptionPoint))
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void addBatchPointTest() throws Exception {
        List<ConsumptionPoint> tmpList = new LinkedList<>();
        for (int i = 0; i < 200; i++) {
            double consumption = ThreadLocalRandom.current().nextDouble(0.0, 31.9);
            ConsumptionPoint consumptionPoint = new ConsumptionPoint("Test", consumption);
            tmpList.add(consumptionPoint);
            // 5ms warten, um bessere Punkte zu kriegen
            Thread.sleep(5);
        }
        String requestBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tmpList);
        this.mockMvc.perform(post("/load/addPoints")
                        .content(requestBody)
                        .contentType("application/json"))
                    .andExpect(status().isOk());
    }

    @Test
    public void getAllPoints() throws Exception {
        Thread.sleep(200);
        MvcResult mvcResult = mockMvc.perform(get("/load/getByName").param("name", "Test")).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ConsumptionPoint> list = mapper.readValue(response, List.class);
        Assertions.assertTrue(list.size() >= 101);
    }
}
