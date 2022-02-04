# Auditing
- 엔티티를 생성과 변경시 변경한 사람과 시간을 추적할때 사용 사스템 구성시 보통 다음 네가지를 기본으로 하고 구성한다.
  - 등록일 등록일
  - 수정일 
  - 등록자 
  - 수정자 
- `@PrePersist` `@PreUpdate`, `@PostPersist`, `@PostUpdate`등 순수 JPA기능을 더 쉽게 활용할 수 있는 기능을 제공
  - 기능 활성화시 `@Configuration` 클래스(`@SpringBootApplication`)에 `@EnableJpaAuditing`을 **반드시 설정**해야함 !!!!!!!!!!
  - `@EntityListeners(AuditingEntityListener.class)` - 엔티티에 적용
    - `@CreatedDate`
    - `@LastModifiedDate`
    - `@CreatedBy` - `AuditorAware<Type>`을 구현한 빈을 등록한 후 사용
    - `@LastModifiedBy` - AuditorAware<Type>을 구현한 빈을 등록한 후 사용
      - 다만 `@EnableJpaAuditing(modifyOnCreate = false)` 설정하면 엔티티 생성시 `@LastModifiedDate`필드로 설정된 부분을 `NULL`로 **세팅**한다.
  - `@EntityListeners(AuditingEntityListener.class)`를 **클래스 구현때마다 붙이는게 귀찮을 때 !!!**
    - `META-INF/orm.xml`에 아래의 내용을 설정하고 엔티티전체에서 공통으로 실행할 수 있도록 한다
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" 
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd" 
                    version="2.2">
        <persistence-unit-metadata>
            <persistence-unit-defaults>
                <entity-listeners>
                    <entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener" />
                </entity-listeners>
            </persistence-unit-defaults>
        </persistence-unit-metadata>
    </entity-mappings>
    ```

# 구현

## `@SpringBootApplication`이 설정된 클래스에 `@EnableJpaAuditing` 설정
```java
@EnableJpaAuditing
@SpringBootApplication
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}
}
```

## Auditing을 위한 베이스 클래스 작성
```java
/**
 * 특정테이블에서 생성 변경된 시간정보만 필요한 경우에 사용
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 공통적으로 사용하는 필드를 묶어 놓은 클래스라는 것을 알려주는 애너테이션 
@Getter
public abstract class BaseTimeEntity {
    
    @CreatedDate
    @Column(updatable = false) //등록일은 수정못하게 막는다.
    private LocalDateTime createDate;
    
    @LastModifiedDate
    private LocalDateTime updatedDate;
}

/**
 * 특정 엔티티에서 데이터를 생성하거나 변경한 사람 추적에 사용
 * BseTimeEntity를 상속함
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass // 공통적으로 사용하는 필드를 묶어 놓은 클래스라는 것을 알려주는 애너테이션 
@Getter
public abstract class BaseEntity extends BaseTimeEntity {
    
    @CreatedBy
    @Column(updatable = false)
    private String createBy; //등록자
    
    @LastModifiedBy
    private String lastModifiedBy; //수정자
}


@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter @Setter
public abstract class JpaBaseEntity {
    
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
```

## Auditing을 위한 베이스 클래스를 상속
```java
/**
 * BaseEntity를 상속한 Member 엔티티
 */
@Entity
@Getter @Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})
public class Member extends BaseEntity {
    
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    
    @NonNull
    private String userName;
    private int age;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    
    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if (!Objects.isNull(team)) {
            changeTeam(team);
        }
    }
    
    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }
    
    /**
     * 연관관계 편의 메서드
     */
    public void changeTeam(Team team) {
        this.team = team;
        if (!Objects.isNull(id)) {
            team.getMembers().removeIf(m -> m.getId().equals(id)); 
        }
        team.getMembers().add(this);
    }
}



/**
 * JpaBaseEntity를 상속한 MemberJpaEntity 엔티티
 */
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
```