package com.study.datajpa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    
    @CreatedDate
    @Column(updatable = false) //등록일은 수정못하게 막는다.
    private LocalDateTime createDate;
    
    @LastModifiedDate
    private LocalDateTime updatedDate;
    
    @CreatedBy
    @Column(updatable = false)
    private String createBy; //등록자
    
    @LastModifiedBy
    private String lastModifiedBy; //수정자
}
