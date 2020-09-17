package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotValidException;
import domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.thoughtworks.rslist.api.UserController.userList;


@RestController
public class RsController {

  private List<RsEvent> rsList = initRsEvent();

  public RsController() throws SQLException {
  }

  private List<RsEvent> initRsEvent() throws SQLException {
    createTableByJdbc();
    List<RsEvent> rsEventList = new ArrayList<>();
    User user = new User("wang", "female", 18, "c@thoughtworks.com", "12222222222");
    rsEventList.add(new RsEvent("鸡肉降价了", "经济", user));
    rsEventList.add(new RsEvent("中国女排八连胜", "体育", user));
    rsEventList.add(new RsEvent("湖北复航国际客运航线", "社会时事", user));
    return rsEventList;
  }

  private static void createTableByJdbc() throws SQLException {
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rsSystem?user=root&password=&useUnicode=true&characterEncoding=utf8&useSSL=false");
    DatabaseMetaData metaData = connection.getMetaData();
    ResultSet resultSet = metaData.getTables(null,null,"rsEvent",null);
    if (!resultSet.next()) {
      String creatTableSql = "create table rsEvent(eventName varchar(200) not null, keyword varchar(100) not null)";
      Statement statement = connection.createStatement();
      statement.execute(creatTableSql);
    }
    connection.close();
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
    } else if (start <1 || end > rsList.size()){
      throw new RsEventNotValidException("invalid request param");
    }
    return ResponseEntity.ok(rsList.subList(start - 1, end));
  }

  @PostMapping("/rs/event")
  public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent){
    if (UserController.isUserExist(rsEvent.getUser())) {
      rsList.add(rsEvent);
    } else {
      rsList.add(rsEvent);
      userList.add(rsEvent.getUser());
    }
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

  @ExceptionHandler(RsEventNotValidException.class)
  public ResponseEntity rsExceptionHandler(RsEventNotValidException e) {
    Error error = new Error();
    error.setError(e.getMessage());
    return ResponseEntity.badRequest().body(error);
  }
}

