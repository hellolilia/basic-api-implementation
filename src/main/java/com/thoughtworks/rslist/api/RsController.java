package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
import domain.User;
import domain.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@Validated
public class RsController {

  @Autowired
  RsEventRepository rsEventRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  VoteRepository voteRepository;
  @Autowired
  RsService rsService;

  Logger logger = LoggerFactory.getLogger(RsController.class);

  public RsController() throws SQLException {
  }


  @GetMapping("/rs/{index}")
  public ResponseEntity getOneRsEvent(@PathVariable int index) {
    List<RsEvent> rsEvents = rsEventRepository.findAll().stream()
            .map(item ->RsEvent.builder().eventName(item.getEventName())
                    .keyWord(item.getKeyWord()).userId(item.getUserPO().getId())
                    .voteNum(item.getVoteNum()).build())
            .collect(Collectors.toList());
    if (index <= 0 || index > rsEvents.size()) {
      throw new RsEventNotValidException("invalid index");
    }
    return ResponseEntity.ok( rsEvents.get(index - 1));
  }

  @GetMapping("/rs/list")
  public ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    List<RsEvent> rsEvents = rsEventRepository.findAll().stream()
            .map(item ->RsEvent.builder().eventName(item.getEventName())
                    .keyWord(item.getKeyWord()).userId(item.getUserPO().getId())
                    .voteNum(item.getVoteNum()).build())
            .collect(Collectors.toList());
    if ( start == null || end == null){
      return ResponseEntity.ok(rsEvents);
    } else if (start <=0 || end > rsEvents.size()){
      throw new RsEventNotValidException("invalid request param");
    }
    return ResponseEntity.ok(rsEvents.subList(start - 1, end));
  }

  @PostMapping("/rs/event")
  public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent){
    Optional<UserPO> userPO = userRepository.findById(rsEvent.getUserId());
    if (!userPO.isPresent()) {
      return ResponseEntity.badRequest().build();
    }
    RsEventPO rsEventPO = RsEventPO.builder().keyWord(rsEvent.getKeyWord()).eventName(rsEvent.getEventName())
            .userPO(userPO.get()).build();
    rsEventRepository.save(rsEventPO);
    return ResponseEntity.created(null).build();
  }

  @DeleteMapping("/rs/{index}")
  public ResponseEntity deleteOneRsEvent(@PathVariable int index){
    List<RsEvent>  rsEvents = rsEventRepository.findAll().stream()
            .map(item ->RsEvent.builder().eventName(item.getEventName())
                    .keyWord(item.getKeyWord()).userId(item.getUserPO().getId())
                    .voteNum(item.getVoteNum()).build())
            .collect(Collectors.toList());
    rsEvents.remove(index - 1);
    return ResponseEntity.ok(rsEvents);
  }

  @PatchMapping("/rs/{rsEventId}")
  public  ResponseEntity modifyOneRsEvent(@PathVariable int rsEventId, @RequestBody RsEvent modifyEvent){
    RsEventPO rsEventPo = rsEventRepository.findById(rsEventId).get();
    if ( modifyEvent.getUserId() == rsEventPo.getUserPO().getId()) {
      if (modifyEvent.getEventName() != null) {
        rsEventPo.setEventName(modifyEvent.getEventName());
      }
      if (modifyEvent.getKeyWord() != null) {
        rsEventPo.setKeyWord(modifyEvent.getKeyWord());
      }
      rsEventRepository.save(rsEventPo);
      return ResponseEntity.ok().build();
    }else {
      return ResponseEntity.badRequest().build();
  }
  }

  @ExceptionHandler({RsEventNotValidException.class, MethodArgumentNotValidException.class})
  public ResponseEntity rsExceptionHandler(Exception e) {
    String errorMessage;
    if(e instanceof MethodArgumentNotValidException) {
      errorMessage = "invalid param";
    } else {
      errorMessage = e.getMessage();
    }
    logger.error(errorMessage);
    Error error = new Error();
    error.setError(errorMessage);
    return ResponseEntity.badRequest().body(error);
  }

  @PostMapping("/rs/vote/{rsEventId}")
  public ResponseEntity vote(@PathVariable int rsEventId, @RequestBody Vote vote) {
    vote.setRsEventId(rsEventId);
    rsService.vote(vote);
    return ResponseEntity.ok().build();
  }
}

