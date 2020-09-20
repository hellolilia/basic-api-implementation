package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;

    Logger logger = LoggerFactory.getLogger(UserController.class);


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

    @GetMapping("/user/{userId}")
    public ResponseEntity getUser(@PathVariable int userId){
        Optional<UserPO> userPO = userRepository.findById(userId);
        return ResponseEntity.ok(userPO);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity deleteUser(@PathVariable int userId){
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity rsExceptionHandler(Exception e) {
        Error error = new Error();
        error.setError("invalid user");
        logger.error("invalid user");
        return ResponseEntity.badRequest().body(error);
    }
}
