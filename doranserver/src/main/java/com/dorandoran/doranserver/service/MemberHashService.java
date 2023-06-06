package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.Member;
import com.dorandoran.doranserver.entity.MemberHash;

import java.util.List;

public interface MemberHashService {
    List<MemberHash> findHashByMember(Member member);
}
