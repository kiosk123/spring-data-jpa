# 도메인 클래스 컨버터 
- HTTP 파라미터로 넘어온 엔티티의 아이디(PK)로 엔티티 객체를 찾아서 바인딩
  - 이렇게 조회한 엔티티는 **단순 조회용으로만 사용**해야한다 -> **트랜잭션이 없는 범위에서 엔티티를 조회**했으므로, **엔티티를 변경해도 DB에 반영되지 않음**

## 예제

```java
@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }
    
    /**
     * 도메인 클래스 컨버팅
     * id 파라미터(Member의 PK)로 member 엔티티를 가져와서 자동으로 세팅해준다.
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUserName();
    }
    
    
    @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
```