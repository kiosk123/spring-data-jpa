package com.study.datajpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEAM")
public class TeamJpaEntity extends JpaBaseEntity {
    
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    
    @NonNull
    private String name;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<MemberJpaEntity> members = new ArrayList<>();
}
