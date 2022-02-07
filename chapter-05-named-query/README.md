# 네임드쿼리 - @NamedQuery, @Query, @Param 활용

네임드 쿼리는 애플리케이션 로딩 시점에 파싱하기 때문에 문법오류가 있으면 오류를 뱉어낸다.

```java
@NamedQuery(name = "Member.findByUserName",
            query = "select m from Member m where m.userName = :userName")
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
    
    @Query(name = "Member.findByUsername")
    List<Member> findByUserName(@Param("userName") String userName);
}
```
