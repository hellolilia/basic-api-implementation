package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void should_register_user() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].name",is("xiaowang")))
                .andExpect(jsonPath("$[0].gender",is("female")))
                .andExpect(jsonPath("$[0].age",is(19)))
                .andExpect(jsonPath("$[0].email",is("a@thoughtworks.com")))
                .andExpect(jsonPath("$[0].phone",is("18888888888")))
                .andExpect(status().isOk());
    }

}