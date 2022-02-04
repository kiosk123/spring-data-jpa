# MVC에서 페이징과 정렬 편리하게 처리하기
- 다음의 파라미터들을 클라이언트에서 컨트롤러로 전달하면 자동으로 컨트롤러에서 Pageable타입에 자동으로 바인딩 해준다.
  - `page` - 데이터를 가져올 page 번호 (**0번 부터 시작**), **page만 지정한 경우 한번에 20개씩 가져온다** (아무것도 지정 안 할시 첫번째 페이지 번호로 설정)
  - `sort` - 특정 컬럼을 기준으로 정렬하는데 사용한다. sort=정렬대상프로퍼티명,<asc|desc>형태로 사용한다.  
            정렬 컬럼 대상이 여러개이면 정렬 컬럼 대상이 여러개이면 `sort=userName,desc&sort=age,asc...` 이런식으로 파라미터를 전달한다.
  - `size` - 한페이지에 노출할 데이터 건수
  - ex) `page=1&sort=id,desc&sort=userName,desc`
- 파라미터에 아무것도 설정안하면 디폴트 페이지 사이즈 만큼 데이터를 가져오는데 디폴트값은 **20**이다
  - 이 값을 변경하고 싶으면 application.yml의 `data.web.pageable.default-page-size`옵션에 값을 할당해주면된다.(글로벌 설정)
  - 특정 URL에만 페이지 사이즈 값을 조절하고 싶으면 `@PageableDefault`를 활용한다. 
  - 컨트롤러에서 처리되는 `Pageable`이 둘이상이면 `@Qualifier`에 접두사를 추가한다. 
        - ex) `@Qualifier("member") -> member_page=0&member_size=10`
- page파라미터 시작 페이지값을 0이아닌 1로 시작하려면 application.yml에 `spring.data.web.pageable.one-indexed-parameters` 옵션을 `true`로 설정하면 되지만  
    **응답되는 페이지 시작번호의 값은 여전히 0부터 시작하는 문제**가 있다 (되도록 **기본 옵션으로 할 것을 권장**)

# 예제
## 컨트롤러 
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
     * id 파라미터로 member 엔티티를 가져와서 자동으로 세팅해준다.
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUserName();
    }
    
    
    /**
     *  MVC에서 페이징처리 예제에서는 엔티티를 반환하지만 실제로는 DTO로 변환해서 반환하자!!!
     *  page - 데이터를 가져올 page 번호(0번 부터 시작), page만 지정한 경우 한번에 20개씩 가져온다(아무것도 지정안할시 첫번째 페이지 번호로 설정)
     *  sort - 특정 컬럼을 기준으로 정렬하는데 사용한다. sort=정렬대상프로퍼티명,<asc,desc>형태로 사용한다.
     *         정렬 컬럼 대상이 여러개이면 sort=userName,desc&sort=age,asc... 이런식으로 넘긴다
     *  size - 한페이지에 노출할 데이터 건수
     *  ex) page=1&sort=id,desc&sort=userName,desc
     */
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 15, sort = {"userName"})Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    
    /**
     * 실제로는 paging처리 후 API 반환할 때 다음과 같이 DTO로 변환해서 반환한다.
     */
    @GetMapping("/memberdtos")
    public Page<MemberDTO> listToDto(@PageableDefault(size = 15, sort = {"userName"})Pageable pageable) {
        return memberRepository.findAll(pageable)
                               .map(member -> new MemberDTO(member.getId(), 
                                                            member.getUserName(),
                                                            member.getAge(),
                                                            member.getTeam() == null ? null : member.getTeam().getName()));
    }
    
    
    /**
     * 컨트롤러에서 처리되는 Pageable이 둘이상이면 @Qualifier에 접두사를 추가하고
     * 접두사 추가시 다음과 같은 형태로 파라미터가 바인딩 된다.
     * @Qualifier("member") -> member_size=10&member_page=1,...
     * 
     * 이 예제에서는 엔티티를 그대로 반환하지만 실제로는 DTO로 변환해서 반환해야한다.!!!!!!!!! 중요!!!!!!!!!!
     * ex) Page<MemberDTO> toMap = page.map(member -> new MemberDTO(member.getId(), member.getUserName(), member.getTeam().getName()));
     */
    @GetMapping("/members_and_teams")
    public MemberAndTeamPageDTO<Member, Team> getMemberAndOrderPage(@Qualifier("member") Pageable mPagable, 
                                                      @Qualifier("team") Pageable tPageable) {
        Page<Member> members = memberRepository.findAll(mPagable);
        Page<Team> teams = teamRepository.findAll(tPageable);
        return new MemberAndTeamPageDTO<>(members, teams);
    }
    
    
    @AllArgsConstructor
    @Getter @Setter
    static class MemberAndTeamPageDTO<T,U> {
        Page<T> members;
        Page<U> teams;
    }
    
    /**
     * 데이터 세팅
     */
    @PostConstruct
    public void init() {
        IntStream.range(0, 100).forEach(n -> {
            memberRepository.save(new Member("user" + n, n, null));
            memberRepository.save(new Member("user" + n, n + 1, null));
            teamRepository.save(new Team("team" + n));
        });
    }
}
```