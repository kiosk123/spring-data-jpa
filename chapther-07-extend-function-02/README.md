# Auditing
- 엔티티를 생성과 변경시 변경한 사람과 시간을 추적할때 사용 사스템 구성시 보통 다음 네가지를 기본으로 하고 구성한다.
  - 등록일 등록일
  - 수정일 
  - 등록자 
  - 수정자 
- `@PrePersist` `@PreUpdate`, `@PostPersist`, `@PostUpdate`등 순수 JPA기능을 더 쉽게 활용할 수 있는 기능을 제공
  - 기능 활성화시 `@Configuration` 클래스(`@SpringBootApplication`)에 `@EnableJpaAudting`을 **반드시 설정**해야함 !!!!!!!!!!
  - `@EntityListeners(AuditingEntityListener.class)` - 엔티티에 적용
    - `@CreatedDate`
    - `@LastModifiedDate`
    - `@CreatedBy` - `AuditorAware<Type>`을 구현한 빈을 등록한 후 사용
    - `@LastModifiedBy` - AuditorAware<Type>을 구현한 빈을 등록한 후 사용
      - 다만 `@EnableJpaAuditing(modifyOnCreate = false)` 설정하면 엔티티 생성시 `@LastModifiedDate`필드로 설정된 부분을 `NULL`로 **세팅**한다.
  - `@EntityListeners(AuditingEntityListener.class)`를 **클래스 구현때마다 붙이는게 귀찮을 때 !!!**
    - `META-INF/orm.xml`에 아래의 내용을 설정하고 엔티티전체에서 공통으로 실행할 수 있도록 한다
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" 
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd" 
                    version="2.2">
        <persistence-unit-metadata>
            <persistence-unit-defaults>
                <entity-listeners>
                    <entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener" />
                </entity-listeners>
            </persistence-unit-defaults>
        </persistence-unit-metadata>
    </entity-mappings>
    ```
