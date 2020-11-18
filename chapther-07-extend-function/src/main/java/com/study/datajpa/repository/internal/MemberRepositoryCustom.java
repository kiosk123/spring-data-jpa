package com.study.datajpa.repository.internal;

import java.util.List;

import com.study.datajpa.domain.Member;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
