package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RsControllerTest {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        voteRepository.deleteAll();
        UserPO userPO = UserPO.builder().age(25).email("ab@c.com").gender("male")
                .phone("19999999999").username("xiaowang").voteNum(10).build();
        userRepository.save(userPO);
        rsEventRepository.save(RsEventPO.builder().keyWord("经济")
                .eventName("鸡肉降价了").userPO(userPO).voteNum(0).build());
        rsEventRepository.save(RsEventPO.builder().keyWord("体育")
                .eventName("中国女排八连胜").userPO(userPO).voteNum(0).build());
        rsEventRepository.save(RsEventPO.builder().keyWord("社会时事")
                .eventName("湖北复航国际客运航线").userPO(userPO).voteNum(0).build());
    }

    @Test
    @Order(1)
    public void should_get_event_list() throws Exception {
        List<UserPO> allUser = userRepository.findAll();
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
    public void should_add_event_when_user_exist() throws Exception {
        UserPO saveUser = userRepository.save(UserPO.builder().username("xiaohong").age(20).phone("16666666666")
                .email("xh@b.com").gender("female").voteNum(10).build());

        String jsonString = "{\"eventName\":\"信条上映\",\"keyWord\":\"文化\",\"userId\":" + saveUser.getId() + "}";

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<RsEventPO> all = rsEventRepository.findAll();
        assertNotNull(all);
        assertEquals(4,all.size());
        assertEquals("信条上映",all.get(3).getEventName());
        assertEquals("文化",all.get(3).getKeyWord());
        assertEquals(saveUser.getId(),all.get(3).getUserPO().getId());

    }

    @Test
    @Order(5)
    public void should_not_add_event_when_user_not_exist() throws Exception {

        String jsonString = "{\"eventName\":\"信条上映\",\"keyWord\":\"文化\",\"userId\":100}";

        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void should_delete_one_rs_event() throws Exception {

        mockMvc.perform(delete("/rs/2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("湖北复航国际客运航线")))
                .andExpect(jsonPath("$[1].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void should_throw_invalid_index_when_get_wrong_index() throws Exception {
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid index")));
        mockMvc.perform(get("/rs/100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid index")));
    }

    @Test
    @Order(7)
    public void should_throw_invalid_request_param_when_get_wrong_param() throws Exception {
        mockMvc.perform(get("/rs/list?start=10&end=30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=-1&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
        mockMvc.perform(get("/rs/list?start=1&end=6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }

    @Test
    @Order(8)
    public void should_modify_one_rs_event() throws Exception {
        List<UserPO> allUser = userRepository.findAll();
        List<RsEventPO> allRsEvent = rsEventRepository.findAll();
        String jsonString = "{\"eventName\":\"张纪中结婚\",\"keyWord\":\"娱乐\",\"userId\": " + allUser.get(0).getId() + "}";

        mockMvc.perform(patch("/rs/{rsEventId}",allRsEvent.get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("张纪中结婚")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void should_modify_eventName_when_no_keyword() throws Exception {
        List<UserPO> allUser = userRepository.findAll();
        List<RsEventPO> allRsEvent = rsEventRepository.findAll();
        String jsonString = "{\"eventName\":\"张纪中结婚\",\"userId\": " + allUser.get(0).getId() + "}";

        mockMvc.perform(patch("/rs/{rsEventId}",allRsEvent.get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("张纪中结婚")))
                .andExpect(jsonPath("$[0].keyWord", is("经济")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    public void should_modify_keyword_when_no_eventName() throws Exception {
        List<UserPO> allUser = userRepository.findAll();
        List<RsEventPO> allRsEvent = rsEventRepository.findAll();
        String jsonString = "{\"keyWord\":\"娱乐\",\"userId\": " + allUser.get(0).getId() + "}";

        mockMvc.perform(patch("/rs/{rsEventId}",allRsEvent.get(0).getId()).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("鸡肉降价了")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[1].eventName", is("中国女排八连胜")))
                .andExpect(jsonPath("$[2].keyWord", is("社会时事")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    public void should_add_Vote_to_mysql() throws Exception {
        UserPO user = userRepository.findAll().get(0);
        RsEventPO rsEvent = rsEventRepository.findAll().get(0);
        String jsonString =String.format("{\"userId\":%d,\"time\":\"%s\",\"voteNum\":5}",
                        user.getId(), LocalDateTime.now().toString());
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEvent.getId()).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
