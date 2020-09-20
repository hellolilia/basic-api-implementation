package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import domain.Vote;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RsService {
    final RsEventRepository rsEventRepository;
    final UserRepository userRepository;
    final VoteRepository voteRepository;

    public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }


    public ResponseEntity vote(Vote vote, int rsEventId) {
        Optional<RsEventPO> rsEventPO = rsEventRepository.findById(rsEventId);
        Optional<UserPO> userPO = userRepository.findById(vote.getUserId());
        if (!rsEventPO.isPresent() || !userPO.isPresent() || vote.getVoteNum() > userPO.get().getVoteNum()) {
            throw new RuntimeException();
        }
        VotePO votePO = VotePO.builder().localDateTime(vote.getVoteTime()).num(vote.getVoteNum())
                .rsEventPO(rsEventPO.get()).userPO(userPO.get()).build();
        voteRepository.save(votePO);
        UserPO user = userPO.get();
        user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
        userRepository.save(user);
        RsEventPO rsEvent = rsEventPO.get();
        rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
        rsEventRepository.save(rsEvent);
        return ResponseEntity.ok().build();
    }

}
