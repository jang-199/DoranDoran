package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface MemberService {
    public Optional<Member> findByEmail(String email);

    public void saveMember(Member member);
}
