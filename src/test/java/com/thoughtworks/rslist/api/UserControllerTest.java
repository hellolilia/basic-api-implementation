package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import domain.RsEvent;
import domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {


    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        UserPO userPO = UserPO.builder().age(25).email("ab@c.com").gender("male")
                .phone("19999999999").username("xiaowang").build();
        userRepository.save(userPO);
        RsEventPO rsEventPO = RsEventPO.builder().keyWord("经济")
                .eventName("鸡肉降价了").userPO(userPO).build();
        rsEventRepository.save(rsEventPO);
    }
    @Test
    @Order(1)
    public void should_register_user() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<UserPO> allUser = userRepository.findAll();
        assertEquals(2, allUser.size());
        assertEquals("xiaowang", allUser.get(1).getUsername());
        assertEquals("a@thoughtworks.com", allUser.get(1).getEmail());
    }

    @Test
    @Order(2)
    public void name_should_less_than_8() throws Exception {
        User user = new User("xiaowanggg", "female", 19, "a@thoughtworks.com", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    public void age_should_between_18_and_100_test1() throws Exception {
        User user = new User("xiaowang", "female", 15, "a@thoughtworks.com", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void gender_should_not_null() throws Exception {
        User user = new User("xiaowang", null, 19, "a@thoughtworks.com", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void age_should_between_18_and_100_test2() throws Exception {
        User user = new User("xiaowang", "female", 150, "a@thoughtworks.com", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void email_should_suit_format() throws Exception {
        User user = new User("xiaowang", "female", 19, "athoughtworks", "18888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void phone_should_suit_format() throws Exception {
        User user = new User("xiaowang", "female", 19, "a@thoughtworks.com", "28888888888");
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    public void should_get_user_by_userid() throws Exception {
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void shoule_delete_user() throws Exception {
        UserPO userPO = UserPO.builder().age(25).email("ab@c.com").gender("male")
                .phone("19999999999").username("dawang").build();
        userRepository.save(userPO);
        RsEventPO rsEventPO = RsEventPO.builder().keyWord("体育")
                .eventName("中国女排八连胜").userPO(userPO).build();
        rsEventRepository.save(rsEventPO);
        rsEventPO = RsEventPO.builder().keyWord("社会时事")
                .eventName("湖北复航国际客运航线").userPO(userPO).build();
        rsEventRepository.save(rsEventPO);
        mockMvc.perform(delete("/user/{id}",userPO.getId()))
                .andExpect(status().isOk());
        assertEquals(1,userRepository.findAll().size());
        assertEquals(1,rsEventRepository.findAll().size());
    }
}