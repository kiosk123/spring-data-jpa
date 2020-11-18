package com.study.datajpa.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class AuditingBeanConfiguration {
    
    /**
     * 여기서는 이렇게 했지만 실무에서는 스프링 시큐리티 컨텍스트 또는 서블릿 컨텍스트에서 
     * 홀더나 세션정보를 가져와서
     * 유저 아이디를 넣어준다
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                //((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
                return Optional.of(UUID.randomUUID().toString());
            }
        };
    }
}
