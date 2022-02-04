package com.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaRepositories("com.study.datajpa.repository")
//@EnableJpaAuditing(modifyOnCreate = false) //엔티티 생성시 @LastModifiedDate필드로 설정된 부분을 NULL로 세팅한다.
@EnableJpaAuditing
@SpringBootApplication
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}
}
