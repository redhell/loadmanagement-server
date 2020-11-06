package de.bublitz.balancer.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bublitz.balancer.server.model.Anschluss;
import de.bublitz.balancer.server.model.ChargeBox;
import de.bublitz.balancer.server.model.Consumer;
import de.bublitz.balancer.server.repository.AnschlussRepository;
import de.bublitz.balancer.server.repository.ChargeboxRepository;
import de.bublitz.balancer.server.repository.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AnschlussControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ChargeboxRepository chargeboxRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private AnschlussRepository anschlussRepository;

    @Autowired
    private MockMvc mockMvc;
    private String jsonString;
    private ObjectMapper mapper = new ObjectMapper();

    @AfterClass
    public void tearDown() {
        consumerRepository.deleteAll();
        chargeboxRepository.deleteAll();
        anschlussRepository.deleteAll();
    }

    @Test
    public void createEmptyAnschluss() throws Exception {
        Anschluss anschluss = new Anschluss();
        anschluss.setName("TestAnschluss");
        anschluss.setMaxLoad(123);
        jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(anschluss);
        this.mockMvc.perform(post("/anschluss/add").content(jsonString).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test(dependsOnMethods = "createEmptyAnschluss")
    public void addChargeboxToAnschluss() throws Exception {
        // Chargebox anlegen und persistieren
        ChargeBox chargeBox = new ChargeBox();
        chargeBox.setName("Anschluss_Test_CB");
        chargeboxRepository.save(chargeBox);

        this.mockMvc
                .perform(get("/anschluss/addChargebox")
                        .param("anschlussName", "TestAnschluss")
                        .param("chargeboxName", "Anschluss_Test_CB"))
                .andExpect(status().isOk());
    }

    @Test(dependsOnMethods = "createEmptyAnschluss")
    public void addConsumerToAnschluss() throws Exception {
        // Consumer anlegen und persistieren
        Consumer consumer = new Consumer();
        consumer.setName("Anschluss_Test_Consumer");
        consumerRepository.save(consumer);

        this.mockMvc
                .perform(get("/anschluss/addConsumer")
                        .param("anschlussName", "TestAnschluss")
                        .param("consumerName", "Anschluss_Test_Consumer"))
                .andExpect(status().isOk());
    }
}
