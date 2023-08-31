package com.dorandoran.doranserver.domain.member.service;

import com.dorandoran.doranserver.domain.member.domain.AccountClosureMember;

import java.util.Optional;

public interface AccountClosureMemberService {
    Optional<AccountClosureMember> findClosureMemberByEmail(String email);

    void saveClosureMember(AccountClosureMember accountClosureMember);

    void deleteScheduledClosureMember();
}
