# Spring Data JPA 공부
* Spring Data JPA에 대한 공부를 정리한 것으로 사용방법은 리포지토리 클래스와 테스트케이스를 참고  

## 구성정보
* JPA 2.2
* java 버전 11
* bootstrap v4.3.1
* Thymeleaf 3
* Thymeleaf 이클립스 플러그인 - http://www.thymeleaf.org/eclipse-plugin-update-site/

## 스프링 프로젝트 구성하기
* [Spring Initializr 사이트 활용](https://start.spring.io/)

## API 테스트
* [POSTMAN](https://www.postman.com/)
* [Katalon](https://www.katalon.com/)

## 참고사이트
 - [Spring 가이드 문서](https://spring.io/guides)
 - [Spring Boot 참고 문서](https://docs.spring.io/spring-boot/docs/)
 - [쿼리 파라미터 로그 남기기](https://github.com/gavlyukovskiy/spring-boot-data-source-decorator)
    - 그레이들에서 다음과 같이 설정
    - implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6' 
    - 운영에서는 사용하지 말 것
    
 - [테스트시 Unable to Find @SpringBootConfiguration 오류 해결 ](https://www.baeldung.com/spring-boot-unable-to-find-springbootconfiguration-with-datajpatest)
    - @테스트시 @SpringBootApplication이 설정된 클래스의 패키와 같은 경로거나 하위 경로로 패키지 경로를 맞춰줘야 실행됨

## 챕터별 설명
 - 챕터 1 : 프로젝트 구성
    - SpringDataJPA를 이용해서 리포지토리 구성시 반드시 인터페이스를 이용하여 구성
        - 리포지토리 인터페이스가 JpaRepository를 상속받아 구현한다. 
 - 챕터 2 : 예제 도메인 구성
 - 챕터 3 : Spring Data JPA를 이용한 리포지토리 생성
    - 공통인퍼테이스 설정 @EnableJpaRepositories(basePackages = "")
    - Spring Boot는 위의 과정도 자동화
    - 리포지토리 인터페이스(중요!!!)에서 JpaRepository<엔티티타입, PK타입> 인터페이스를 구현
 - 챕터 4 : 메소드 이름으로 쿼리 생성
    - [쿼리 메소드 키워드 참고 1.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)
    - [쿼리 메소드 키워드 참고 2.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result)
 - 챕터 5 : 네임드쿼리
    - @NamedQuery, @Query, @Param 활용
 - 챕터 6 : 리포지토리에 쿼리 정의
    - @Query, @Param 활용 
    - 단순 값과 DTO 조회
    - [반환 가능한 타입](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types)
    - 페이징과 정렬
        - 페이징과 정렬 파라미터
            - Sort : 정렬기능
            - Pageable : 페이징 기능(내부에 Sort 포함)
        - 반환타입
            - Page : 추가 count 쿼리 결과를 포함하는 페이징
                - count 쿼리는 상황에 따라 성능저하의 요인이 될 수 있기 때문에 @Query(countQuery="")를 이용해서 count 쿼리를 분리할 수 있다.
            - Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조히) - 모바일에서 더 보기 기능에 유용
            - List (자바 컬렉션) : 추가 count 쿼리 없이 결과만 반환 
 