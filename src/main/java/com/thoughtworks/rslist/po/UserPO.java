package com.thoughtworks.rslist.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.javafx.beans.IDProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "user")
@Data
public class UserPO {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name")
    private String username;
    private String gender;
    private int age;
    private String email;
    private String phone;
    private int voteNum = 10;
}
