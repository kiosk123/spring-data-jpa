package com.study.datajpa.dto;

/**
 * 중첩 프로젝션
 */
public interface NestedClosedProjections {
    
    String getUserName();
    TeamInfo getTeam();
    
    interface TeamInfo {
        String getName();
    }
}
