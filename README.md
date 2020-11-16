# Spring Data JPA 공부

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