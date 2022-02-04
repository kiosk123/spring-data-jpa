# 리포지토리 메소드에 쿼리 정의 - @Query, @Param 활용 
  
기본적으로 메소드에 `@Query`를 붙이고 `@Query`에 `JPQL`을 선언함으로써 해당 `@Query`이 붙은 메서드를 호출하면 `@Query`에 선언된 `JPQL`이 실행된다.
- 기본적으로 `Select` 쿼리에는 `@Query`와 `@Param` 조합을 사용한다. `@Param`를 이용해서 파라미터에 값을 바인딩한다.
- `@Query`에 JPQL을 이용해서 DTO를 반환할 수 있다
- 페이징 쿼리시 `@Query`의 `countQuery` 옵션에 전체 카운트 쿼리를 조회할 수 있다. 그리고 이런식으로 처리시 페이징 처리를 위해 반환타입은 `Page<T>` 또는 `Slice<T>`타입으로 반환한다.
  - 페이징과 정렬 파라미터
     - `org.springframework.data.domain.Sort` : 정렬 기능
     - `org.springframework.data.domain.Pageable` : 페이징 기능 (내부에 Sort 포함)
  - 특별한 반환 타입
     - `org.springframework.data.domain.Page` : 추가 count 쿼리 결과를 포함하는 페이징
     - `org.springframework.data.domain.Slice` : 추가 count 쿼리 없이 다음 페이지만 확인 가능 (내부적으로 limit + 1조회)
     - `List (자바 컬렉션)`: 추가 count 쿼리 없이 결과만 반환
- `Update`나 `Delete`등 벌크성 수정쿼리 실행할 때는 `@Query`와 `@Modifying`을 동시에 사용해야 한다. 벌크성 수정쿼리 실행시는 영속성 컨텍스트 초기화가 필요한데 `@Modifying`은 영속성 컨텍스트를 초기화하는 역할을 한다. 단 쿼리 메소드를 이용하여 조회할때는 영속성 컨텍스트에서 데이터를 가져오기 때문에 엔티티매니저를 통해`em.clear()`를 해줘야한다.  
(같은 트랜잭션상에서 사용되는 `EntityManager`는 동일함을 JPA는 보장) 그리고 반환 타입은 `int`나 `void`만 허용한다.
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

## 테스트 코드
```java
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryQueryTest {
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TeamRepository teamRepository;
    
    @PersistenceContext
    EntityManager em;
    
    Set<String> names = new HashSet<>();
    
    @BeforeEach
    public void settingMemberData() {
        
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("AAA", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member5", 10, teamA);
        Member member6 = new Member("member6", 10, teamA);
        Member member7 = new Member("member7", 10, teamA);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        
        names.add(member1.getUserName());
        names.add(member2.getUserName());
        names.add(member3.getUserName());
        names.add(member4.getUserName());
        names.add(member5.getUserName());
        names.add(member6.getUserName());
        names.add(member7.getUserName());
    }
    
    @Test
    public void findByUserNameAndGreaterThen() {

        List<Member> collect = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
    
    @Test
    public void findByUserName() {
        String userName = "member1";
        
        List<Member> collect = memberRepository.findByUserName(userName);
        
        assertEquals(1, collect.size());
        assertEquals("member1", collect.get(0).getUserName());
    }
    
    @Test 
    public void findUser() {
        List<Member> collect = memberRepository.findUser("AAA", 20);
        
        assertEquals(1, collect.size());
        assertThat(20).isEqualTo(collect.get(0).getAge());
        assertThat("AAA").isEqualTo(collect.get(0).getUserName());
    }
    
    @Test
    public void findUserNameList() {
        List<String> collect = memberRepository.findUserNameList();
       
        assertEquals(7, collect.size());
        assertEquals(7, names.size());
        collect.forEach(name -> {
            if (!names.contains(name)) {
                fail(name + " is not in findUserNameList");
            }
        });
    }
    
    @Test
    public void findMemberDTO() {
        List<MemberDTO> collect = memberRepository.findMemberDTO();
       
        assertEquals(7, collect.size());
        assertEquals(7, names.size());
        collect.forEach(dto -> {
            if (!names.contains(dto.getUserName())) {
                fail(dto.getUserName() + " is not in findMemberDTO");
            }
        });
        
    }
    

    @Test
    public void findByNames() {
        List<String> userNames = new ArrayList<>(names);
        
        List<Member> collect = memberRepository.findByNames(userNames);
        
        assertEquals(7, collect.size());
    }
    
    /**
     * 페이징 테스트
     */
    @Test
    public void findByAge() {
        /*
         * 페이지 번호(0 부터 시작), 가져올 데이터 수, 정렬 기준은 userName 프로퍼티를 기준으로 내림차순으로
         */
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
        
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        List<Member> content = page.getContent();
        Long totalCount = page.getTotalElements();
        
        
        //-- then --//
        // 3개를 정확하게 가져왔는가?
        assertEquals(3, content.size()); 
        
        // 전체 데이터수가 4개인가
        assertEquals(4, totalCount); 
        
        // 현재 페이지 번호
        // 현재 페이지번호가 0이 맞는가?
        assertEquals(0, page.getNumber()); 
        
        //전체페이지 개수
        //전체 페이지 갯수는 2개여야 된다. (전체데이터 4개에서 1페이지가 3개로 이루어짐으로)
        assertEquals(2, page.getTotalPages()); 
        
        // 현재페이지가 첫번째 페이지인가
        assertThat(page.isFirst()).isTrue();
        
        // 다음페이지가 있은가?
        assertThat(page.hasNext()).isTrue();
        
        // 이전페이지가 존재하는가?
        assertThat(page.hasPrevious()).isFalse();
        
        // page 타입을 DTO로 변환
        Page<MemberDTO> toMap = page.map(member -> new MemberDTO(member.getId(), member.getUserName(), member.getTeam().getName()));
        
        
    }
    
    /**
     * 슬라이스 Slice 테스트
     */
    @Test
    public void findByAgeIntoSlice() {
        /*
         * 페이지 번호(0 부터 시작), 가져올 데이터 수, 정렬 기준은 userName 프로퍼티를 기준으로 내림차순으로
         */
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));
       
        Slice<Member> slice = memberRepository.findByAgeIntoSlice(age, pageRequest);
        
        List<Member> content = slice.getContent();
        
        //-- then --//
        //3개를 정확하게 가져왔는가?
        /**
         * 실제로는 select ... limit 4; 과 같이
         * 실제로는 4개를 가져옴 -> 요청한 데이터 갯수 + 1
         * 하지만 List에는 요청한 데이터 갯 수만큼의 데이터가 담김
         * 모바일의 더 보기 기능에 유용(전체 페이지 갯수를 구하지 않고, 다음페이지 존재 여부를 알 수 있기 때문)
         */
        assertEquals(3, content.size()); 
        
        // 현재 페이지 번호
        // 현재 페이지번호가 0이 맞는가?
        assertEquals(0, slice.getNumber()); 
        
        // 현재페이지가 첫번째 페이지인가
        assertThat(slice.isFirst()).isTrue();
        
        // 다음페이지가 있은가?
        assertThat(slice.hasNext()).isTrue();
        
        // 이전페이지가 존재하는가?
        assertThat(slice.hasPrevious()).isFalse();
    }
    
    @Test
    public void buikAgePlus() {
        Member member = new Member("BBB", 20);
        memberRepository.save(member);
        
        int updateCount = memberRepository.bulkAgePlus(20);
        
        assertEquals(4, updateCount);
        
        Member findMember = memberRepository.findById(member.getId()).get();
        
        /**
         * 원래 나이는 20살에 20 + 1이므로 21살이 되어야한다.
         * 하지만 실제로는 20살이 나온다.
         * 그 이유는 JPQL, @NamedQuery(결국은 JPQL을 query name으로 매핑한 것)로 select하고
         * 영속성 컨텍스트에서 select로 가줘온 엔티티를 반환되지만.
         * 
         * JPQL이 아닌 EntityManager의 find()나 @Query를 통한 데이터를 가져오지 않는 쿼리 이름 메소드(ex. findById)로
         * 데이터를 가져온 경우에는 DB가 아닌 먼저 영속성 컨텍스트에 있는 것을 먼저조회하기 때문에
         * 벌크 연산 후에는 영속성 컨텍스트를 flush()와 clear()를 같이하거나 clear() 해줘야 한다.
         * 
         * 가장 좋은 건 벌크연산만 실행하고 딱 끝나는 것이 좋다.!!!
         */
        assertNotEquals(21, findMember.getAge());
        
        /**
         * 같은 트랜잭션 상에서 EntityManager의 동일성을 스프링은 보장한다.
         */
        em.clear();
        
        /**
         * 21살로 정확히 다시 조회된다.
         */
        findMember = memberRepository.findById(member.getId()).get();
        assertEquals(21, findMember.getAge());
        
    }
    
    /**
     * Query 힌트 테스트
     */
    @Test
    public void queryHint() {
        Member member = new Member("newMember", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();
        
        /**
         * findById는 다음과 같이 설정되어 있음
         * @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
         * 위와 같이 설정하면 반환된 엔티티를 변경하고 flush()나 commit()해도 DB에는 반영안됨
         * 스냅샷을 만들지 않기 때문에 조회 성능이 좋아지고 조회 쿼리에만 사용하는데
         * 무작정 사용하지 말고 정말 조회 트래픽이 너무 많아서 부하가 심각한 곳에 여러번 생각후에 설정한다.
         */
        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUserName("member2"); //변경해도 변경감지 안됨
        em.flush();
    }
    
    /**
     * JPA 락 테스트
     */
    @Test
    public void lock() {
        Member member = new Member("newMember", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();
        
        /*
         *  select
                member0_.member_id as member_i1_0_,
                member0_.age as age2_0_,
                member0_.team_id as team_id4_0_,
                member0_.user_name as user_nam3_0_ 
            from
                member member0_ 
            where
                member0_.user_name=? for update
            
            실행시 위 처럼 쿼리에 for update가 붙는 걸 볼 수 있음
         */
        
        Member findMember = memberRepository.findLockByUserName(member.getUserName()).get(0);
        findMember.setUserName("member2"); 
        em.flush();
    }
    
}

```