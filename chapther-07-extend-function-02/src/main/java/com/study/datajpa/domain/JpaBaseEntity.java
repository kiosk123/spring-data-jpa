package com.study.datajpa.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter @Setter
public class JpaBaseEntity {
    
    @Column(updatable = false) //등록일은 수정못하게 막는다.
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;
    
    @PrePersist //em.persist시 영속성 컨텍스트에 등록 사용전에 호출됨
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updatedDate = now;
    }
    
    @PreUpdate //업데이트 전에 호출
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
