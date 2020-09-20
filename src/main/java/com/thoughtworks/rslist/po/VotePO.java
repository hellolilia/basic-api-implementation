package com.thoughtworks.rslist.po;

import domain.RsEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vote")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotePO {
    @Id
    @GeneratedValue
    private int id;
    private int num;
    private LocalDateTime localDateTime;
    @ManyToOne
    @JoinColumn(name = "rs_event_id")
    private RsEventPO rsEventPO;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserPO userPO;
}
