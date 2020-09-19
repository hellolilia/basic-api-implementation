package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    public static List<User> userList = initUser();
    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private static List<User> initUser() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("wang", "female", 20, "w@thoughtworks.com", "12222222222"));
        userList.add(new User("yang", "male", 18, "y@thoughtworks.com", "12333333333"));
        userList.add(new User("ming", "female", 25, "m@thoughtworks.com", "16666666666"));
        return userList;
    }


    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user){
        UserPO userPO = new UserPO();
        userPO.setUsername(user.getName());
        userPO.setAge(user.getAge());
        userPO.setGender(user.getGender());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNum(user.getVoteNum());
        userRepository.save(userPO);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/user")
    public ResponseEntity getUserList(){
        return ResponseEntity.ok(userList);
    }


    @GetMapping("/users")
    public ResponseEntity getUsers(){
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

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity rsExceptionHandler(Exception e) {
        Error error = new Error();
        error.setError("invalid user");
        logger.error("invalid user");
        return ResponseEntity.badRequest().body(error);
    }
}
