# 프로젝트 구성 및 스프링 데이터 JPA 실행
![.](./img/1.png)  

# application.yml 파일 구성
```yml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/datajpa
    username: sa
    password: 
    driver-class-name: org.h2.Driver
    
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
#        show_sql: true # System.out을 통해 출력
        format_sql: true
        use_sql_comments: true
        dialect: org.hibernate.dialect.H2Dialect
        default_batch_fetch_size: 100

logging:
  level:
    org.hibernate.SQL: debug #logger를 통해 출력
    org.hibernate.type: trace #SQL 쿼리 파라미터를 확인할 수 있다
```

# JpaRepository 인터페이스 구현

```java
/**
 * JpaRepository<엔티티타입, 엔티티PK타입>
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>{
    
}
```

# 테스트 코드
```java
@ActiveProfiles("test")
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    public void test() {
        Member member = new Member("memberA");
        memberRepository.save(member);
        
        /** 
         * JpaRepository 인터페이스를 구현하면 기본적으로 findById 메서드가 제공됨
         * 반환 타입은 Optional<T>
         */
        Optional<Member> optional = memberRepository.findById(member.getId());
        
        Member findMember = null;
        if (optional.isPresent()) {
            findMember = optional.get();
        }
        
        assertNotNull(findMember);
        assertEquals(findMember.getId(), member.getId());
        
        // assertEquals(findMember.getUserName(), member.getUserName()); // 아래와 동일
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    }

}
```