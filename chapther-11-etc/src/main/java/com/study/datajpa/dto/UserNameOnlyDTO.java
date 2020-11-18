package com.study.datajpa.dto;

public class UserNameOnlyDTO {
    
    private final String userName;
    
    //클래스 기반 프로젝션시 생성자 파라미터 이름이 조회할 타겟 엔티티의 프로퍼티 명과 동일해야함
    public UserNameOnlyDTO(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
