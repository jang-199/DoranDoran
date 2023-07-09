package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.lockType.LockType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LockMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCK_MEMBER_ID")
    private Long lockMemberId;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member memberId;

    @Column(name = "LOCK_START_TIME")
    private LocalDateTime lockStartTime;

    @Column(name = "LOCK_END_TIME")
    private LocalDateTime lockEndTime;

    @Enumerated(EnumType.STRING)
    private LockType lockType;

    public LockMember(Member memberId, Duration lockDay, LockType lockType) {
        this.memberId = memberId;
        this.lockStartTime = LocalDateTime.now();
        this.lockEndTime = LocalDateTime.now().plusDays(lockDay.toDays());
        switch ((int) lockDay.toDays()) {
            case 1 -> this.lockType = LockType.Day1;
            case 7 -> this.lockType = LockType.Day7;
            case 30 -> this.lockType = LockType.Day30;
            default -> this.lockType = LockType.Ban;
        }
    }
}
