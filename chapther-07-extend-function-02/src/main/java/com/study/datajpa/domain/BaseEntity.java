package com.study.datajpa.domain;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity {
    
    @CreatedBy
    @Column(updatable = false)
    private String createBy; //등록자
    
    @LastModifiedBy
    private String lastModifiedBy; //수정자
}
