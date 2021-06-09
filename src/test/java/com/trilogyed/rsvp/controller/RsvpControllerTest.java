package com.trilogyed.rsvp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilogyed.rsvp.dao.RsvpDao;
import com.trilogyed.rsvp.model.Rsvp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RsvpController.class)
public class RsvpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RsvpDao dao;

    private ObjectMapper mapper = new ObjectMapper();

    private Rsvp daoRsvp;
    private String daoJson;
    private List<Rsvp> allRsvps = new ArrayList<>();
    private String allRsvpsJson;

    @Before
    public void setup() throws Exception {
        daoRsvp = new Rsvp();
        daoRsvp.setId(9999);
        daoRsvp.setGuestName("Sam Smith");
        daoRsvp.setTotalAttending(3);

        daoJson = mapper.writeValueAsString(daoRsvp);

        Rsvp rsvp = new Rsvp();
        rsvp.setId(1357);
        rsvp.setGuestName("Julie Jones");
        rsvp.setTotalAttending(2);
        allRsvps.add(daoRsvp);
        allRsvps.add(rsvp);

        allRsvpsJson = mapper.writeValueAsString(allRsvps);

    }

    @Test
    public void shouldCreateNewRsvpOnPostRequest() throws Exception {
        Rsvp inputRsvp = new Rsvp();
        inputRsvp.setGuestName("Sam Smith");
        inputRsvp.setTotalAttending(3);
        String inputJson = mapper.writeValueAsString(inputRsvp);

        given(dao.addRsvp(inputRsvp)).willReturn(daoRsvp);

        mockMvc.perform(
                post("/rsvps")
                .content(inputJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(daoJson));

    }

    @Test
    public void shouldReturnRsvpById() throws Exception {
        given(dao.getRsvp(9999)).willReturn(daoRsvp);

        mockMvc.perform(
                get("/rsvps/9999"))
                .andExpect(status().isOk())
                .andExpect((content().json(daoJson))
        );

    }

    @Test
    public void shouldBStatusOkForNonExistentRsvpId() throws Exception {
        given(dao.getRsvp(1234)).willReturn(null);

        mockMvc.perform(
                get("/rsvps/1234"))
                .andExpect(status().isOk()
        );

    }

    @Test
    public void shouldReturnAllRsvps() throws Exception {
        given(dao.getAllRsvps()).willReturn(allRsvps);

        mockMvc.perform(
                get("/rsvps"))
                .andExpect(status().isOk())
                .andExpect(content().json(allRsvpsJson)
        );
    }

    @Test
    public void shouldUpdateByIdAndReturn200StatusCode() throws Exception {
        mockMvc.perform(
                put("/rsvps")
                        .content(daoJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteByIdAndReturn200StatusCode() throws Exception {
        mockMvc.perform(delete("/rsvps/2")).andExpect(status().isOk());
    }

}