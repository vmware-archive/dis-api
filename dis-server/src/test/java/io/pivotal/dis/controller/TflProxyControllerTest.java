package io.pivotal.dis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.pivotal.dis.config.ApplicationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfiguration.class})
@WebAppConfiguration
public class TflProxyControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getLineDisruptionsReturnsHttpStatusOk() throws Exception {
        mockMvc.perform(get("/lines/disruptions"))
                .andExpect(status().isOk());
    }


    @Test
    public void getLineDisruptionsReturnsValidJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/lines/disruptions"))
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(contentAsString);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Invalid JSON returned");
        }
    }

    @Test
    public void getLineDisruptionsReturnsNorthernLine() throws Exception {
        MvcResult result = mockMvc.perform(get("/lines/disruptions"))
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        assertThat(JsonPath.read(contentAsString, "$.disruptions[0].line"), equalTo("northern"));
    }


}