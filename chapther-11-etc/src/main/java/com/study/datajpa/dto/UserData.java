package com.study.datajpa.dto;

import org.springframework.beans.factory.annotation.Value;

//--오픈 프로젝션--//
public interface UserData {
    
    /**
     * 엔티티의 userName과 age프로퍼티 값을 조합해서 값을 반환함
     */
    @Value("#{target.userName + ' ' + target.age}")
    String getUserData();
}
