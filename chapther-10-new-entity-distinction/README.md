# 새로운 엔티티 구별방법
- 문제  
  - 스프링 데이터 JPA는 **식별자 필드(PK필드)에 값이 없으면 persist하고 있으면 merge**한다.  
  문제는 기존테이블이 분할되어 ID생성 정책이 변경되어 ID를 직접 채번해서 입력해야할 경우가 생기면 persist전에 아이디가 생성된다.  
  이렇게 되면 persist를 호출하는데 아닌 merge를 호출해서 DB에 해당 식별값에 해당하는 데이터가 있는지 select 쿼리를 하고 나서 엔티티를 생성하게 되는 비효율적인 상황이 발생한다.
- 해결  
  - `Persistable<식별(PK)필드 타입>` 인터페이스를 엔티티가 구현하여 새로운 엔티티인지 아닌지 판단하는 로직을 구현한다.

# 예제
```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Item extends BaseEntity implements Persistable<String>{
    
    @Id
    @NonNull
    private String id;

    /**
     * 엔티티가 새거인지 아닌지를 판단하는 로직 구현
     * 여기서는 데이터 생성날짜가 null인 경우 Persist 대상으로 판단한다 
     */
    @Override 
    public boolean isNew() {
        return getCreateDate() == null;
    }
}
```