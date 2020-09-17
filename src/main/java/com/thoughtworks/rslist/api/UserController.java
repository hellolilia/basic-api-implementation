package com.thoughtworks.rslist.api;

import domain.RsEvent;
import domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    public static List<User> userList = initUser();

    private static List<User> initUser() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("wang", "female", 20, "w@thoughtworks.com", "12222222222"));
        userList.add(new User("yang", "male", 18, "y@thoughtworks.com", "12333333333"));
        userList.add(new User("ming", "female", 25, "m@thoughtworks.com", "16666666666"));
        return userList;
    }


    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user){
        userList.add(user);
        return ResponseEntity.created(null).header("index",String.valueOf(userList.size()-1)).build();
    }

    @GetMapping("/user")
    public ResponseEntity getUserList(){
        return ResponseEntity.ok(userList);
    }



    public static boolean isUserExist(User user) {
        List<User> users = userList.stream().filter(user1 -> user1.getName().equals(user.getName())).collect(Collectors.toList());
        if (users.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
