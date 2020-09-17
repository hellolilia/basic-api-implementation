package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.RsEvent;
import domain.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    public void should_register_user() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$",hasSize(4)))
                .andExpect(jsonPath("$[3].name",is("xiaowang")))
                .andExpect(jsonPath("$[3].gender",is("female")))
                .andExpect(jsonPath("$[3].age",is(19)))
                .andExpect(jsonPath("$[3].email",is("a@thoughtworks.com")))
                .andExpect(jsonPath("$[3].phone",is("18888888888")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void name_should_less_than_8() throws Exception {
        User user = new User("xiaowanggg", "female", 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    public void age_should_between_18_and_100_test1() throws Exception {
        User user = new User("xiaowang", "female", 15, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void gender_should_not_null() throws Exception {
        User user = new User("xiaowang", null, 19, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void age_should_between_18_and_100_test2() throws Exception {
        User user = new User("xiaowang", "female", 150, "a@thoughtworks.com", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void email_should_suit_format() throws Exception {
        User user = new User("xiaowang", "female", 19, "athoughtworks", "18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void phone_should_suit_format() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "28888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    public void user_should_register_when_not_exist() throws Exception {
        User user = new User("xiaoyang", "female", 20, "xy@thoughtworks.com", "16688888888");
        RsEvent rsEvent = new RsEvent("添加一条热搜", "娱乐", user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(jsonPath("$[2].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[3].eventName", is("添加一条热搜")))
                .andExpect(jsonPath("$[3].keyWord", is("娱乐")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].name",is("wang")))
                .andExpect(jsonPath("$[0].gender",is("female")))
                .andExpect(jsonPath("$[1].age",is(18)))
                .andExpect(jsonPath("$[1].email",is("y@thoughtworks.com")))
                .andExpect(jsonPath("$[2].phone",is("16666666666")))
                .andExpect(jsonPath("$[2].name",is("ming")))
                .andExpect(jsonPath("$[3].gender",is("female")))
                .andExpect(jsonPath("$[3].age",is(19)))
                .andExpect(jsonPath("$[4].email",is("xy@thoughtworks.com")))
                .andExpect(jsonPath("$[4].phone",is("16688888888")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void user_should_not_register_when_exist() throws Exception {
        User user = new User("wang", "female", 19, "a@thoughtworks.com", "18888888888");
        RsEvent rsEvent = new RsEvent("流星雨来了", "生活", user);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(jsonPath("$[2].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[3].eventName", is("添加一条热搜")))
                .andExpect(jsonPath("$[3].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[4].eventName", is("流星雨来了")))
                .andExpect(jsonPath("$[4].keyWord", is("生活")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].name",is("wang")))
                .andExpect(jsonPath("$[0].gender",is("female")))
                .andExpect(jsonPath("$[1].age",is(18)))
                .andExpect(jsonPath("$[1].email",is("y@thoughtworks.com")))
                .andExpect(jsonPath("$[2].phone",is("16666666666")))
                .andExpect(jsonPath("$[2].name",is("ming")))
                .andExpect(jsonPath("$[3].gender",is("female")))
                .andExpect(jsonPath("$[3].age",is(19)))
                .andExpect(jsonPath("$[4].name",is("xiaoyang")))
                .andExpect(jsonPath("$[4].phone",is("16688888888")))
                .andExpect(status().isOk());
    }

}