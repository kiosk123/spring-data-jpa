package com.study.datajpa.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})
@Table(name = "MEMBER2")
public class MemberJpaEntity extends JpaBaseEntity{
    
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    
    @NonNull
    private String userName;
    private int age;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private TeamJpaEntity team;
    
    public MemberJpaEntity(String userName, int age, TeamJpaEntity team) {
        this.userName = userName;
        this.age = age;
        if (!Objects.isNull(team)) {
            changeTeam(team);
        }
    }
    
    public MemberJpaEntity(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }
    
    /**
     * 연관관계 편의 메서드
     */
    public void changeTeam(TeamJpaEntity team) {
        this.team = team;
        if (!Objects.isNull(id)) {
            team.getMembers().removeIf(m -> m.getId().equals(id)); 
        }
        team.getMembers().add(this);
    }
}
