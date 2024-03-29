# Spring Data JPA 공부
* Spring Data JPA에 대한 공부를 정리한 것으로 사용방법은 리포지토리 클래스와 테스트케이스를 참고  

## 구성정보
* JPA 2.2  
* java 버전 11  
* lombok  
* spring-data-jpa  
* h2 데이터베이스  
* Spring Web MVC  

## gradle 설정
```gradle
plugins {
	id 'org.springframework.boot' version '2.3.5.RELEASE'
	id 'io.spring.dependency-management' version '1.0.10.RELEASE'
	id 'java'
}

group = 'springboot.jpa'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6' //운영에서는 사용하지 말 것
    implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-devtools'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}

test {
	useJUnitPlatform()
}
```

## 현재 프로젝트의 의존관계 보기 gradlew 이용
```bash
./gradlew dependencies —configuration compileClasspath
```

## 스프링 부트 프로젝트 구성하기
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
    - 테스트시 @SpringBootApplication이 설정된 클래스의 패키와 같은 경로거나 하위 경로로 패키지 경로를 맞춰줘야 실행됨

## 스프링 부트 JPA 매핑 테이블및 컬럼 네이밍 규칙
 - 스프링 부트에서 엔티티명과 프로퍼티가 JPA 테이블과 테이블 컬럼과 매핑될때 기본적으로 카멜케이스 + 언더스코어 전략을 사용한다.
    - ex) 프로퍼티명이 userName일 경우 -> USER_NAME 컬럼과 매핑
 - 카멜케이스 + 언더스코어 전략을 사용하지 않을 경우 application.yml 파일 옵션에 다음과 같이 설정한다. [참고](https://www.baeldung.com/hibernate-field-naming-spring-boot)

```yml
spring:
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
```
    

## 챕터별 설명
 - 챕터 1 : 프로젝트 구성 및 스프링 데이터 JPA 실행
    - SpringDataJPA를 이용해서 리포지토리 구성시 반드시 인터페이스를 이용하여 구성
        - 리포지토리 인터페이스가 JpaRepository를 상속받아 구현한다. 
 - 챕터 2 : 예제 도메인 구성
 - 챕터 3 : Spring Data JPA를 이용한 리포지토리 생성
    - 공통인터테이스 설정 활성화 `@EnableJpaRepositories(basePackages = "")`를 `@Configuration` 클래스에 설정 - `@SpringBootApplication`의 하위패키지에서 검색이 불가능할때 만 설정
    - Spring Boot는 위의 과정도 자동화
    - 리포지토리 인터페이스(중요!!!)에서 JpaRepository<엔티티타입, PK타입> 인터페이스를 구현
 - 챕터 4 : 메소드 이름으로 쿼리 생성
    - [쿼리 메소드 키워드 참고 1.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords)
    - [쿼리 메소드 키워드 참고 2.](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result)
 - 챕터 5 : 네임드쿼리
    - @NamedQuery, @Query, @Param 활용
 - 챕터 6 : 리포지토리 메소드에 쿼리 정의
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
       - API로 반환할 때는 엔티티는 DTO로 변환하여 반환한다.
           - map() 메소드를 활용하여 DTO로 매핑한다.
    - 벌크성 수정 쿼리
        - @Modifying을 활용
        - 벌크 연산 후 JPQL이나 @NamedQuery를 활용하여 조회하는 것이 아니면 영속성 컨텍스트에서 엔티티를 꺼내오기 때문에  
          데이터 베이스의 내용이 엔티티에 반영 안 될 경우도 있으니 JPA, JDBC, Mybatis를 활용하여 벌크연산 수행 후에는  
          반드시 영속성 컨텍스트를 [flush()와] clear() 해주고나서 엔티티를 조회해야한다.  
          가장 좋은 것은 벌크 연산만으로 처리가 끝나는 경우가 가장 좋다. 
    - @EntityGraph
        - 연관된 엔티티들을 SQL로 한번에 조회하는 방법
        - JPQL 페치조인 쿼리 작성 없이 @EntityGraph와 attributePaths를 이용하여 연관관계가 맺어진 엔티티를 가져올 수 있다.
    - JPA 힌트 & 락
        - JPA 쿼리 힌트(@QueryHints, @QueryHint) (SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
 - 챕터 7 : 확장
    - 사용자 정의 리포지토리 
        - 용도
            - 인터페이스 메서드 직접 구현시 구현기능이 많아짐 다양한 이유로 직접 특정 메서드만 특정 기능으로 구현하고 싶을 때 사용
            - JPA 직접 사용
            - 스프링 JDBC Template 사용
            - MyBatis 사용
            - 데이터베이스 커넥션 직접 사용
            - Query DSL 사용
       - 구현방법
            - 특정 기능이 정의된 인터페이스 생성 (ex) MemberRepositoryCustom
            - 특정 기능을 구현할 클래스 생성 후 구현(ex) MemberRepositoryImpl implements MemberRepositoryCustom
            - 생성할 클래스 명은 Spring Data JPA 기능 정의 인터페이스 명 + Impl 규칙을 지켜야함
                - Spring Data JPA 기능 정의 인터페이스 명이 MemberRepository 명이면,
                - 생성 클래스 명은 MemberRepositoryImpl
                    - Impl 대신 다른 이름을 사용하고 싶다면 @EnableJpaRepository 애너테이션의 옵션을 활용한다.
                        - (ex) @EnableJpaRepositories(basePackages = "study.datajpa.repository", repositoryImplementationPostfix = "Impl")
            - 특정 기능의 정의된 인터페이스를 Spring Data JPA 기능 정의 인터페이스가 상속 받게 함
                - (ex) MemberRepository extends JpaRepository<T,ID>, MemberRepositoryCustom
       - 권장사항 중요!!!!!
            - 항상 사용자 정의 리포지토리가 필요한 것은 아니고 그냥 임의의 리포지토리를 만들어서 스프링 빈으로 등록해서 직접 사용해도된다.
            - 사용자 정의 리포지토리를 구현하면 Spring Data JPA 정의 인터페이스의 혼잡도가 증가됨
            - 예를 들어 순수 엔티티 조회용과 DTO(뷰나 API에서 사용)변환용 기능이 다 정의되면 혼잡할 수 있음
            - 이럴 때는 단순한 엔티티 조회용은 Spring Data JPA 인터페이스로 정의하고 복잡한 비즈니스 로직은 따로 스프링빈으로 정의하여 빼는 것이 좋음 
    - Auditing
       - 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적할때 사용 사스템 구성시 보통 다음 네가지를 기본으로 하고 구성한다.
           - 등록일
           - 수정일
           - 등록자
           - 수정자
       - @PrePersist @PreUpdate, @PostPersist, @PostUpdate등 순수 JPA기능을 더 쉽게 활용할 수 있는 기능을 제공
           - 기능 활성화시 @Configuration 클래스(@SpringBootApplication)에 @EnableJpaAuditing 반드시 설정해야함 !!!!!!!!!!
           - @EntityListeners(AuditingEntityListener.class) - 엔티티에 적용
               - @CreatedDate
               - @LastModifiedDate
               - @CreatedBy - AuditorAware<Type>을 구현한 빈을 등록한 후 사용
               - @LastModifiedBy - AuditorAware<Type>을 구현한 빈을 등록한 후 사용
                   - 다만 @EnableJpaAuditing(modifyOnCreate = false) 설정하면 엔티티 생성시 @LastModifiedDate필드로 설정된 부분을 NULL로 세팅한다.
           - @EntityListeners(AuditingEntityListener.class)를 클래스 구현때마다 붙이는게 귀찮을 때
               - META-INF/orm.xml에 내용을 설정하고 엔티티전체에서 공통으로 실행할 수 있도록 한다
                   - META-INF/orm.xml 파일 내용은 프로젝트 디렉터리의 orm.xml의 내용을 참고 할 것
  
 - 챕터 8 : 도메인 클래스 컨버터 
    - HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
    - 이렇게 조회한 엔티티는 단순 조회용으로만 사용해야한다 -> 트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않음
 - 챕터 9 : MVC에서 페이징과 정렬 편리하게 처리하기
    - 다음의 파라미터들을 클라이언트에서 컨트롤러로 전달하면 자동으로 컨트롤러에서 Pageable타입에 자동으로 바인딩 해준다.
       - page - 데이터를 가져올 page 번호(0번 부터 시작), page만 지정한 경우 한번에 20개씩 가져온다(아무것도 지정안할시 첫번째 페이지 번호로 설정)
       - sort - 특정 컬럼을 기준으로 정렬하는데 사용한다. sort=정렬대상프로퍼티명,<asc|desc>형태로 사용한다.  
                정렬 컬럼 대상이 여러개이면 정렬 컬럼 대상이 여러개이면 sort=userName,desc&sort=age,asc... 이런식으로 파라미터를 전달한다.
       - size - 한페이지에 노출할 데이터 건수
       - ex) page=1&sort=id,desc&sort=userName,desc 
    - 파라미터에 아무것도 설정안하면 디폴트 페이지 사이즈 만큼 데이터를 가져오는데 디폴트값은 20이다
       - 이 값을 변경하고 싶으면 application.yml의 data.web.pageable.default-page-size옵션에 값을 할당해주면된다.(글로벌 설정)
       - 특정 URL에만 페이지 사이즈 값을 조절하고 싶으면 @PageableDefault를 활용한다. 
       - 컨트롤러에서 처리되는 Pageable이 둘이상이면 @Qualifier에 접두사를 추가한다. 
           - ex) @Qualifier("member") -> member_page=0&member_size=10
    - page파라미터 시작 페이지값을 0이아닌 1로 시작하려면 application.yml에 spring.data.web.pageable.one-indexed-parameters 옵션을 true로 설정하면 되지만  
      응답되는 페이지 시작번호의 값은 여전히 0부터 시작하는 문제가 있다 (되도록 기본 옵션으로 할 것을 권장)
 - 챕터 10 : 새로운 엔티티 구별방법
    - 문제  
      스프링 데이터 JPA는 식별자 필드(PK필드)에 값이 없으면 persist하고 있으면 merge한다.  
      문제는 기존테이블이 분할되어 ID생성 정책이 변경되어 ID를 직접 채번해서 입력해야할 경우가 생기면 persist전에 아이디가 생성된다.  
      이렇게 되면 persist를 호출하는데 아닌 merge를 호출해서 DB에 해당 식별값에 해당하는 데이터가 있는지 select 쿼리를 하고 나서  
      엔티티를 생성하게 되는 비효율적인 상황이 발생한다.
    - 해결  
      Persistable<식별(PK)필드 타입> 인터페이스를 엔티티가 구현하여 새로운 엔티티인지 아닌지 판단하는 로직을 구현한다.
 - 챕터 11  
   - Projections : 필요한 데이터만 추출할때 사용
       - 구현 방식 :
          - 인터페이스 
          - 클래스
          - 제네릭 타입 - 위에서 정의한 인터페이스와 클래스를 이용해서 동적으로 프로젝션하는 방법
       - 프로젝션 방식
          - close 프로젝션 : 필요한 데이터만 쿼리해서 가져옴
          - open 프로젝션 : 타겟 엔티티의 데이터를 일단 끌어온 후 필요한 데이터를 추출해서 가져옴
          - nested 프로젝션 : 연관관계로 묶여있는 엔티티의 필드 데이터까지 같이 가져오는 방법 - 연관관계까지 가져오는 복잡한 쿼리일경우 성능에 불리할 수도 있음
   - NativeQuery : 데이터베이스에 직접 SQL을 할때 사용 
        - data jpa 네이티브 쿼리를 사용하는 것 보다 jdbcTeamplate이나 mybatis를 이용하는 것 추천
        - 최대한 네이티브 쿼리 사용을 지양해야함
        - 반환타입과 제약사항은 첨부된 pdf파일 참고
