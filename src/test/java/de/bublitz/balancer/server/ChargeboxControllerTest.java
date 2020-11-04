package de.bublitz.balancer.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChargeboxControllerTest {

    @Autowired
    private ChargeboxRepository chargeboxRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeAll
    public void setUp() {
        chargeboxRepository.deleteAll();
        mapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    public void noChargeboxPresentTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/getAll")).andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ChargeBox> chargeBoxes = mapper.readValue(body, List.class);
        assertEquals(0, chargeBoxes.size());
    }

    @Order(2)
    @Test
    public void addChargeboxByStringTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/add")
                .param("name","test1")
                .param("chargeboxID","test-1")
                .param("startURL","http://start.url/")
                .param("stopURL", "http://stop.url/")).andDo(print()).andExpect(status().isOk()).andReturn();
        assertEquals("true", mvcResult.getResponse().getContentAsString());

        mvcResult = this.mockMvc.perform(get("/chargebox/getAll")).andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ChargeBox> chargeBoxes = mapper.readValue(body, List.class);
        assertEquals(chargeBoxes.size(), 1);
    }

    @Order(3)
    @Test
    public void deleteChargepoxTest() throws Exception {
        this.mockMvc.perform(get("/chargebox/remove").param("name", "test1"))
                .andExpect(status().isOk());
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/getByName").param("name", "test1"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
    }
}
