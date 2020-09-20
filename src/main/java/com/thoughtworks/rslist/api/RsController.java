package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RestController
@Validated
public class RsController {
  private List<RsEvent> rsList = initRsEvent();

  @Autowired
  RsEventRepository rsEventRepository;
  @Autowired
  UserRepository userRepository;

  Logger logger = LoggerFactory.getLogger(RsController.class);

  public RsController() throws SQLException {
  }

  private List<RsEvent> initRsEvent() throws SQLException {

    List<RsEvent> rsEventList = new ArrayList<>();
    User user = new User("wang", "female", 18, "c@thoughtworks.com", "12222222222");
    rsEventList.add(new RsEvent("鸡肉降价了", "经济", 1));
    rsEventList.add(new RsEvent("中国女排八连胜", "体育", 1));
    rsEventList.add(new RsEvent("湖北复航国际客运航线", "社会时事", 1));
    return rsEventList;
  }

  @GetMapping("/rs/{index}")
  public RsEvent getOneRsEvent(@PathVariable int index) {
    if (index <= 0 || index > rsList.size()) {
      throw new RsEventNotValidException("invalid index");
    }
    return rsList.get(index - 1);
  }

  @GetMapping("/rs/list")
  public ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    if ( start == null || end == null){
      return ResponseEntity.ok(rsList);
    } else if (start <=0 || end > rsList.size()){
      throw new RsEventNotValidException("invalid request param");
    }
    return ResponseEntity.ok(rsList.subList(start - 1, end));
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
    return ResponseEntity.created(null).header("index",String.valueOf(rsList.size()-1)).build();
  }

  @DeleteMapping("/rs/{index}")
  public ResponseEntity deleteOneRsEvent(@PathVariable int index){
    rsList.remove(index - 1);
    return ResponseEntity.ok(rsList);
  }

  @PatchMapping("/rs/{index}")
  public  ResponseEntity modifyOneRsEvent(@PathVariable int index, @RequestBody @Valid RsEvent modifyEvent){
      rsList.get(index - 1).setKeyWord(modifyEvent.getKeyWord());
      rsList.get(index - 1).setEventName(modifyEvent.getEventName());
    return ResponseEntity.created(null).build();
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
}

