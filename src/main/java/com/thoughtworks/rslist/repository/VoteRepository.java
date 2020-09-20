package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.po.VotePO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends CrudRepository<VotePO, Integer> {
    @Override
    List<VotePO> findAll();

    @Query(value = "select vote from VotePO vote where vote.localDateTime >:startTime and vote.localDateTime < :endTime")
    List<VotePO> findAllFromStartTimeToEndTime(LocalDateTime startTime, LocalDateTime endTime);
}
