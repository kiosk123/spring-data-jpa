package com.study.datajpa;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.study.datajpa.domain.Item;
import com.study.datajpa.repository.ItemTestRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PersitableInterfaceTest {

    @Autowired
    private ItemTestRepository itemTestRepository;
    
    @Test
    public void persistableTest() {
        Item item = new Item("IJ001");
        itemTestRepository.save(item); //콘솔에서 select 쿼리 호출되지 않은 것을 확인할 수 있음
    }

}
