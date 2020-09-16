package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.RsEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    public void should_get_event_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(jsonPath("$[2].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void should_get_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$.keyWord", is("经济")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/2"))
                .andExpect(jsonPath("$.eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$.keyWord", is("体育")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/3"))
                .andExpect(jsonPath("$.eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$.keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void should_get_rs_event_between() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[0].keyWord", is("体育")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(jsonPath("$[2].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void should_add_one_rs_event() throws Exception {

        RsEvent rsEvent = new RsEvent("信条上映", "文化");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);


        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform((get("/rs/list")))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[1].keyWord", is("体育")))
                .andExpect(jsonPath("$[2].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[3].eventName", is("信条上映")))
                .andExpect(jsonPath("$[3].keyWord", is("文化")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void should_delete_one_rs_event() throws Exception {

        mockMvc.perform(delete("/rs/2"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[2].eventName", is("信条上映")))
                .andExpect(jsonPath("$[2].keyWord", is("文化")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void should_modify_one_rs_event() throws Exception {
        String jsonString = "{\"eventName\":\"张纪中结婚\",\"keyWord\":\"娱乐\"}";

        mockMvc.perform(patch("/rs/1").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("张纪中结婚")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[2].eventName", is("信条上映")))
                .andExpect(jsonPath("$[2].keyWord", is("文化")))
                .andExpect(status().isOk());
    }


    @Test
    @Order(7)
    public void should_modify_one_rs_event_keyword() throws Exception {
        String jsonString = "{\"keyWord\":\"体育\"}";

        mockMvc.perform(patch("/rs/1").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("张纪中结婚")))
                .andExpect(jsonPath("$[0].keyWord", is("体育")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[2].eventName", is("信条上映")))
                .andExpect(jsonPath("$[2].keyWord", is("文化")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void should_modify_one_rs_event_name() throws Exception {
        String jsonString = "{\"eventName\":\"中国女排八连胜\"}";

        mockMvc.perform(patch("/rs/1").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[0].keyWord", is("体育")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(jsonPath("$[2].eventName", is("信条上映")))
                .andExpect(jsonPath("$[2].keyWord", is("文化")))
                .andExpect(status().isOk());
    }
}
