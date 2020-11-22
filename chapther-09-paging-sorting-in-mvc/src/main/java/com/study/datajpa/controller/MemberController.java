package com.study.datajpa.controller;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.study.datajpa.domain.Member;
import com.study.datajpa.domain.Team;
import com.study.datajpa.dto.MemberDTO;
import com.study.datajpa.repository.MemberRepository;
import com.study.datajpa.repository.TeamRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
     *         정렬 컬럼 대상이 여러개이면 sort=userName,desc&sort=age,asc 이런식으로 넘긴다
     *  size - 한페이지에 노출할 데이터 건수
     *  ex) page=1&sort=id,desc&sort=userName,desc
     */
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 15, sort = {"userName"})Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    
    /**
     * 실제로는 pagign처리 후 API 반환할 때 다음과 같이 DTO로 변환해서 반환한다.
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
