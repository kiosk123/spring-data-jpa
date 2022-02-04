# 리포지토리 메소드에 쿼리 정의 - @Query, @Param 활용 
  
기본적으로 메소드에 `@Query`를 붙이고 `@Query`에 `JPQL`을 선언함으로써 해당 `@Query`이 붙은 메서드를 호출하면 `@Query`에 선언된 `JPQL`이 실행된다.
- 기본적으로 `Select` 쿼리에는 `@Query`와 `@Param` 조합을 사용한다. `@Param`를 이용해서 파라미터에 값을 바인딩한다.
- `@Query`에 JPQL을 이용해서 DTO를 반환할 수 있다
- 페이징 쿼리시 `@Query`의 `countQuery` 옵션에 전체 카운트 쿼리를 조회할 수 있다. 그리고 이런식으로 처리시 페이징 처리를 위해 반환타입은 `Page<T>` 또는 `Slice<T>`타입으로 반환한다.
- `Update`나 `Delete`등 벌크성 수정쿼리 실행할 때는 `@Query`와 `@Modifying`을 동시에 사용해야 한다. 벌크성 수정쿼리 실행시는 영속성 컨텍스트 초기화가 필요한데 `@Modifying`은 영속성 컨텍스트를 초기화하는 역할을 한다. 그리고 반환 타입은 `int`나 `void`만 허용한다.
- `@EntityGraph`를 통해서 연관관계로 매핑되어 있는 필드도 같이 조회할 수 있다.
- `@QueryHints`를 이용하여 조회 성능을 올릴 수 있다. 다만 이것을 설정한 메소드가 반환한 엔티티는 더티체킹이 되지 않기 때문에 값이 변경되어도 추적이 안된다.  
조회시 부하가 심한 곳에 고민해서 사용해야한다. 
- `@Lock`를 이용해서 락모드를 설정 할 수 있다.
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    //... 생략

    /**
     * JPQL에 파라미터 매핑
     */
    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);
    
    /**
     * JPQL로 특정 필드만 PROJECTION
     */
    @Query("select m.userName from Member m")
    List<String> findUserNameList();
    
    /**
     * JPQL의 IN쿼리 파라미터로 Collection 넘김
     */
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    
    /**
     * JPQL로 DTO조회해서 반환하기
     */
    @Query("select new com.study.datajpa.dto.MemberDTO(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDTO> findMemberDTO();

   /**
     * totalCount를 가져오는 것은 데이터량이 많아지거나 조인하는 테이블이 많아지면 복잡할 수 있다.
     * totalCount는 조인하여 가져오지 않아도 되는 경우가 많기 때문에 countQuery를 분리하여 사용할 수 있다.
     */
    @Query(value = "select m from Member m left join m.team t where m.age = :age", countQuery = "select count(m.userName) from Member m where m.age = :age")
    Page<Member> findByAge(@Param("age")int age, Pageable pageable);
    
    @Query("select m from Member m where m.age = :age")
    Slice<Member> findByAgeIntoSlice(@Param("age")int age, Pageable pageable);
    
    /**
     * 벌크 수정 쿼리
     */
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    long bulkAgePlus(@Param("age")int age);
    
    
    /*
     * JPQL, @EntityGraph, @EntityGraph + @Query, 메소드 이름 쿼리를 이용한 페치조인 처리
     */
    @Query("select m from Member m left join m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGrapthByUserName(@Param("userName") String userName);
    
    /**
     * JPA 힌트
     * findById는 다음과 같이 설정되어 있음
     * @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
     * 위와 같이 설정하면 반환된 엔티티를 변경하고 flush()나 commit()해도 DB에는 반영안됨
     * 스냅샷을 만들지 않기 때문에 조회 성능이 좋아지고 조회 쿼리에만 사용하는데
     * 무작정 사용하지 말고 정말 조회 트래픽이 너무 많아서 부하가 심각한 곳에 여러번 생각후에 설정한다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);
    
    /**
     * JPA 락
     * 다른 트랜잭션이 접근 못하게 락을 걸어벌임
     * 다음과 같이 설정하면 쓰기때 비관적 락모드가 걸림 
     * @Lock(LockModeType.PESSIMISTIC_WRITE)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);
}
```