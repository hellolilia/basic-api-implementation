package com.thoughtworks.rslist.po;

import domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rsEvent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsEventPO {
    @Id
    @GeneratedValue
    private int id;
    private String eventName;
    private String keyWord;
    @ManyToOne
    private UserPO userPO;


}
