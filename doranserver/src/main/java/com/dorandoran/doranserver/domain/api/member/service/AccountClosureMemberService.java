package com.dorandoran.doranserver.domain.api.member.service;

import com.dorandoran.doranserver.domain.api.member.domain.AccountClosureMember;

import java.util.Optional;

public interface AccountClosureMemberService {
    Optional<AccountClosureMember> findClosureMemberByEmail(String email);

    void saveClosureMember(AccountClosureMember accountClosureMember);

    void deleteScheduledClosureMember();
}
