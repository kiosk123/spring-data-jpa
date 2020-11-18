package com.study.datajpa.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.domain.Persistable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Item extends BaseEntity implements Persistable<String>{
    
    @Id
    @NonNull
    private String id;

    @Override //엔티티가 새거인지 아닌지를 판단하는 로직 구현
    public boolean isNew() {
        return getCreateDate() == null;
    }
}
