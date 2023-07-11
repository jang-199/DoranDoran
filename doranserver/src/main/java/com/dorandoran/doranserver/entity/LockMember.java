package com.dorandoran.doranserver.entity;

import com.dorandoran.doranserver.entity.lockType.LockType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

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

    public Duration RetrieveLockDuration(LockMember lockMember){
        return Duration.between(lockMember.lockStartTime, lockMember.lockEndTime);
    }

    public LockMember(Member memberId, Duration lockDay, LockType lockType) {
        this.memberId = memberId;
        this.lockStartTime = LocalDateTime.now();
        this.lockEndTime = lockType.equals(LockType.Ban)
                ? null
                : LocalDateTime.now().plusDays(lockDay.toDays());
        this.lockType = lockType;
    }

    public LockMember(Member memberId, LocalDateTime lockStartTime, LocalDateTime lockEndTime, LockType lockType) {
        this.memberId = memberId;
        this.lockStartTime = lockStartTime;
        this.lockEndTime = lockEndTime;
        this.lockType = lockType;
    }
}
