package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
import domain.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VoteController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    RsService rsService;

    @GetMapping("/vote/Record")
    public ResponseEntity<List<Vote>> getVoteRecord(@RequestParam String startTime, @RequestParam String endTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime,df);
        LocalDateTime end = LocalDateTime.parse(endTime,df);
        List<VotePO> votePOs = voteRepository.findAllFromStartTimeToEndTime(start,end);
        List<Vote> votes = votePOs.stream().map(item -> Vote.builder().rsEventId(item.getRsEventPO().getId())
                .userId(item.getUserPO().getId()).voteNum(item.getNum()).build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(votes);
    }
}
