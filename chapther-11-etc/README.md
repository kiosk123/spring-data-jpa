# Projections
- Projections : 필요한 데이터만 추출할때 사용
  - 구현 방식 :
    - 인터페이스 
    - 클래스
    - 제네릭 타입 - 위에서 정의한 인터페이스와 클래스를 이용해서 동적으로 프로젝션하는 방법
  - 프로젝션 방식
    - close 프로젝션 : 필요한 데이터만 쿼리해서 가져옴
    ```java
    /**
     * 프로젝션할 필드명으로 인터페이스 선언
     */
    public interface UsernameOnly { 
      String getUsername();  // getter로 선언해야함
    }
     
    /**
     * JpaRepository
     */
    public interface MemberRepository ... {

      /**
       * 메서드 이름은 자유 반환 타입으로 인지
       */
      List<UsernameOnly> findProjectionsByUsername(String username);
    }

    /**
     * 테스트 코드
     */
    @Test 
    public void projections() throws Exception { 
        //given 
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();
        
        //when 
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
        
        //then 
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    ```
    - open 프로젝션 : 타겟 엔티티의 데이터를 일단 끌어온 후 필요한 데이터를 추출해서 가져옴
    ```java
    /**
     * 이렇게 SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다! 따라서 JPQL SELECT 절 최적화가 안된다.
     */
    public interface UsernameOnly {
      @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}") 
      String getUsername();
    }
    ```
    - 클래스 기반 Projection : 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능 - 생성자의 파라미터 이름으로 매칭
    ```java
    public class UsernameOnlyDto {
      private final String username;
      
      /**
       * 생성자의 파라미터 명이 엔티티에서 프로젝션할 필드명과 같아야 한다
       */
      public UsernameOnlyDto(String username) { 
        this.username = username;
      }
      
      public String getUsername() { 
        return username;
      } 
    }

    /**
     * JpaRepository
     */
    public interface MemberRepository ... {

      /**
       * 메서드 이름은 자유 반환 타입으로 인지
       */
      List<UsernameOnlyDto> findProjectionsByUsername(String username);
    }
    ```
    - 동적 Projections : 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
    ```java
    // <T> List<T> findProjectionsByUsername(String username, Class<T> type);

    List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1", UsernameOnly.class);
    ```
    - 중첩 프로젝션 : 연관관계로 묶여있는 엔티티의 필드 데이터까지 같이 가져오는 방법 - 연관관계까지 가져오는 복잡한 쿼리일경우 성능에 불리할 수도 있음
    ```java
    public interface NestedClosedProjection {
      String getUsername();
      TeamInfo getTeam();

      interface TeamInfo { 
        String getName();
      } 
    }

    List<NestedClosedProjection> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjection.class);

    /**
     * 실행되는 쿼리
     * select m.username as col_0_0_
     *      , t.teamid as col_1_0_
     *      , t.teamid as teamid1_2_
     *      , t.name as name2_2_ 
     * from member m 
     *      left outer join team t on m.teamid=t.teamid 
     * where m.username=?
     */
    ```

# Projections 정리
- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능 
- 프로젝션 대상이 ROOT가 아니면 
  - LEFT OUTER JOIN 처리 
  - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
- 프로젝션 대상이 root 엔티티면 유용하다.
- 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
- 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
- 실무에서는 단순할 때만 사용하고, **조금만 복잡해지면 QueryDSL을 사용**하자

# NativeQuery
- NativeQuery : 데이터베이스에 직접 SQL을 할때 사용 
  - data jpa 네이티브 쿼리를 사용하는 것 보다 jdbcTeamplate이나 mybatis를 이용하는 것 추천
  - 최대한 네이티브 쿼리 사용을 지양해야함
  - 반환타입과 제약사항은 첨부된 pdf파일 참고

# 스프링 데이터 JPA 기반 네이티브 쿼리
- 페이징 지원 
- 반환 타입
  - `Object[]` 
  - `Tuple` 
  - `DTO(스프링 데이터 인터페이스 Projections 지원)`
- 제약
  - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리) 
  - JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
  - 동적 쿼리 불가

## Native 쿼리 예제
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
  // 네이티브 쿼리 예제
  @Query(value = "select * from member where username = ?", nativeQuery = true) 
  Member findByNativeQuery(String username);
```
- JPQL은 위치 기반 파리미터를 1부터 시작하지만 네이티브 SQL은 0부터 시작
- 네이티브 SQL을 엔티티가 아닌 DTO로 변환은 하려면 
  - DTO 대신 JPA `TUPLE` 조회 
  - DTO 대신 `MAP` 조회 
  - `@SqlResultSetMapping` 복잡 Hibernate ResultTransformer를 사용해야함 복잡 
  - https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa-and-hibernate/
  - 네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or myBatis 권장


# Projections 활용
스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용

```java
public interface MemberProjection {
  Long getId();
  String getUsername();
  String getTeamName();
}


@Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " + 
               "FROM member m left join team t", 
               countQuery = "SELECT count(*) from member", 
               nativeQuery = true) 
Page<MemberProjection> findByNativeProjection(Pageable pageable);

Page<MemberProjection> result = findByNativeProjection(PageRequest.of(0, 10));
```

동적 네이티브 쿼리
- 하이버네이트를 직접 활용 하거나 스프링 JdbcTemplate, myBatis, jooq같은 외부 라이브러리 사용
```java
// 하이버네이트 기능 사용
//given 
String sql = "select m.username as username from member m";

List<MemberDto> result = em.createNativeQuery(sql) 
                           .setFirstResult(0) 
                           .setMaxResults(10) 
                           .unwrap(NativeQuery.class) 
                           .addScalar("username") 
                           .setResultTransformer(Transformers.aliasToBean(MemberDto.class)) 
                           .getResultList();
```
