package com.study.datajpa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/**
 * 특정테이블에서 시간정보만 필요한 경우에 사용
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {
    
    @CreatedDate
    @Column(updatable = false) //등록일은 수정못하게 막는다.
    private LocalDateTime createDate;
    
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
