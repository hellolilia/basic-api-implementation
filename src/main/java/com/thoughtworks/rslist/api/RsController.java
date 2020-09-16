package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {

  private List<RsEvent> rsList = initRsEvent();

  private List<RsEvent> initRsEvent() {
    List<RsEvent> rsEventList = new ArrayList<>();
    rsEventList.add(new RsEvent("鸡肉降价了", "经济"));
    rsEventList.add(new RsEvent("中国女排八连胜", "体育"));
    rsEventList.add(new RsEvent("湖北复航国际客运航线", "社会时事"));
    return rsEventList;
  }

  @GetMapping("/rs/{index}")
  public RsEvent getOneRsEvent(@PathVariable int index){
    return rsList.get(index - 1);
  }

  @GetMapping("/rs/list")
  public List<RsEvent> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end){
    if ( start == null || end == null){
      return rsList;
    }
    return rsList.subList(start - 1, end);
  }

  @PostMapping("/rs/event")
  public void addRsEvent(@RequestBody RsEvent rsEvent){
    rsList.add(rsEvent);
  }

  @DeleteMapping("/rs/{index}")
  public List<RsEvent> deleteOneRsEvent(@PathVariable int index){
    rsList.remove(index - 1);
    return rsList;
  }

  @PatchMapping("/rs/{index}")
  public  void modifyOneRsEvent(@PathVariable int index, @RequestBody RsEvent modifyEvent){
    if(modifyEvent.getEventName() != null && modifyEvent.getKeyWord() != null){
      rsList.get(index - 1).setKeyWord(modifyEvent.getKeyWord());
      rsList.get(index - 1).setEventName(modifyEvent.getEventName());
    } else if(modifyEvent.getEventName() == null && modifyEvent.getKeyWord() != null){
      rsList.get(index - 1).setKeyWord(modifyEvent.getKeyWord());
    } else {
      rsList.get(index - 1).setEventName(modifyEvent.getEventName());
    }
  }

}

