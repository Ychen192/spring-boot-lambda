package com.yan.springbootlambda.integration;

import com.yan.springbootlambda.SpringBootDemoApplication;
import com.yan.springbootlambda.model.TrainStation;
import com.yan.springbootlambda.service.TransitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SpringBootDemoApplication.class)
@AutoConfigureMockMvc
public class TransitControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransitService transitService;

    @Test
    public void provideStationNamesGivenLine() throws Exception {
        given(transitService.fetchTransitStops('f'))
                .willReturn(List.of(new TrainStation("F01", "Jamaica-179 St")));

        mvc.perform(
                get("/stops/f").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stationId").value("F01"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stationName").value("Jamaica-179 St"));
    }

    @Test
    public void provideLiveTrainTimeGivenStationId() throws Exception {
        var time = Instant.now()
                .atZone(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss"));
        given(transitService.fetchTransitTime("F34"))
                .willReturn(List.of(time));

        mvc.perform(get("/train/time/F34").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(time));
    }
}
