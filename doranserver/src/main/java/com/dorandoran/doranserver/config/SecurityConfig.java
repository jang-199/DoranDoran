package com.dorandoran.doranserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().ignoringAntMatchers("/api/signup");
//                .and()
//                .authorizeRequests()
//                .mvcMatchers("/index","/assets/**","/test").permitAll()
////                .mvcMatchers("/list/**").permitAll()
//                .mvcMatchers("/list/**").hasRole("USER")
//                .anyRequest().authenticated();
        return http.build();

    }
}
