package de.bublitz.balancer.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class ChargeboxControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ChargeboxRepository chargeboxRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeClass
    public void setUp() {
        chargeboxRepository.deleteAll();
        mapper = new ObjectMapper();
    }

    @Test
    public void noChargeboxPresentTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/getAll")).andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ChargeBox> chargeBoxes = mapper.readValue(body, List.class);
        assertEquals(0, chargeBoxes.size());
    }

    @Test(dependsOnMethods = "noChargeboxPresentTest")
    public void addChargeboxByStringTest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/add")
                .param("name", "test1")
                .param("evseid", "test-1")
                .param("startURL", "http://start.url/")
                .param("stopURL", "http://stop.url/")).andExpect(status().isOk()).andReturn();
        assertEquals("true", mvcResult.getResponse().getContentAsString());

        mvcResult = this.mockMvc.perform(get("/chargebox/getAll")).andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ChargeBox> chargeBoxes = mapper.readValue(body, List.class);
        assertEquals(chargeBoxes.size(), 1);
    }

    @Test(dependsOnMethods = "addChargeboxByStringTest")
    public void deleteChargepoxTest() throws Exception {
        this.mockMvc.perform(delete("/chargebox/remove").param("name", "test1"))
                .andExpect(status().isOk());
        MvcResult mvcResult = this.mockMvc.perform(get("/chargebox/getByName").param("name", "test1"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void addBulkChargebox() throws Exception {
        ChargeBox chargeBox = new ChargeBox();
        chargeBox.setName("Test-Box");
        chargeBox.setEvseid("Test-Box");
        String cbJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chargeBox);
        this.mockMvc.perform(post("/chargebox/add").content(cbJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        this.mockMvc.perform(delete("/chargebox/remove").param("name", "Test-Box"))
                .andExpect(status().isOk());
    }

}
