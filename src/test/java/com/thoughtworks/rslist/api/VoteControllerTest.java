package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoteControllerTest {
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
    public void should_get_vote_record() throws Exception {
        UserPO user = userRepository.findAll().get(0);
        RsEventPO rsEvent = rsEventRepository.findAll().get(0);
        String jsonString =String.format("{\"userId\":%d,\"voteTime\":\"%s\",\"voteNum\":2}",
                user.getId(), LocalDateTime.now().toString());
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEvent.getId()).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        RsEventPO rsEvent2 = rsEventRepository.findAll().get(1);
        String jsonString2 =String.format("{\"userId\":%d,\"voteTime\":\"%s\",\"voteNum\":3}",
                user.getId(), String.valueOf(LocalDateTime.of(2020, Month.SEPTEMBER, 5, 0, 0, 0)).toString());
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEvent2.getId()).content(jsonString2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        RsEventPO rsEvent3 = rsEventRepository.findAll().get(2);
        String jsonString3 =String.format("{\"userId\":%d,\"voteTime\":\"%s\",\"voteNum\":5}",
                user.getId(), String.valueOf(LocalDateTime.of(2020, Month.SEPTEMBER, 20, 0, 0, 0)).toString());
        mockMvc.perform(post("/rs/vote/{rsEventId}", rsEvent3.getId()).content(jsonString3)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vote/Record")
                .param("startTime", "2020-09-10 12:00:00")
                .param("endTime","2020-09-30 12:00:00"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId",is(user.getId())))
                .andExpect(jsonPath("$[0].rsEventId",is(rsEvent.getId())))
                .andExpect(jsonPath("$[0].voteNum",is(2)))
                .andExpect(jsonPath("$[1].voteNum",is(5)));

    }

}
