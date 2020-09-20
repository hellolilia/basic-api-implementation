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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


}
