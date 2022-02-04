# 사용자 정의 리포지토리 

- 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성  
- 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음  
- 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?  
  - JPA 직접 사용( EntityManager )
  - 스프링 JDBC Template 사용
  - MyBatis 사용
  - 데이터베이스 커넥션 직접 사용 등등...
  - Querydsl 사용

# 구현 순서
## 1. 사용자 정의 리포지토리 인터페이스 생성
```java
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
```

## 2. 사용자 정의 리포지토리 인터페이스 구현
```java
/**
 * 순수한 JPA로 구현하는 리포지토리 
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;
    
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                 .getResultList();
    }
}
```

## 3. 사용자 정의 리포지토리 인터페이스를 JpaRepository를 상속한 인터페이스에 추가 상속
```java
public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom {
    //... 생략
}
```


## 4. 테스트 코드
```java
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryCustomTest {

    @Autowired
    private MemberRepository memberRepository;
    
    @BeforeEach
    public void settingMemberData() {
                
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        Member member3 = new Member("member3", 30);
        Member member4 = new Member("member4", 40);
        Member member5 = new Member("member5", 50);
        Member member6 = new Member("member6", 60);
        Member member7 = new Member("member7", 70);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
       
    }
    
    @Test
    public void callCustom() {
        /**
         * MemberRepositoryCustom 인터페이스가 아닌 MemberRepository에서 MemberRepositoryCustom의 구현 메서드를 호출
         */
        List<Member> result = memberRepository.findMemberCustom();
        
        assertEquals(7, result.size());
    }

}
```

# 주의사항
사용자 정의 리포지토리 구현 클래스 접미사에 `JpaRepository인터페이스명 + Impl`형태로 이름을 맞춰야함
```java
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
// ...
}

public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom {
    //... 생략
}
```

`Impl`을 사용하기 싫다면 `@EnableJpaRepositories(basePackages = "study.datajpa.repository", repositoryImplementationPostfix = "Impl")`에서  
`repositoryImplementationPostfix`값을 변경해주면 됨

```java

@EnableJpaRepositories(basePackages = "com.study.datajpa.repository", repositoryImplementationPostfix = "Custom")
@SpringBootApplication
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

}
```