package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.entity.AccountClosureMember;

import java.util.Optional;

public interface AccountClosureMemberService {
    Optional<AccountClosureMember> findClosureMemberByEmail(String email);

    void saveClosureMember(AccountClosureMember accountClosureMember);

    void deleteScheduledClosureMember();
}
