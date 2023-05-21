package com.dorandoran.doranserver.config;

import com.dorandoran.doranserver.config.jwt.JwtAuthenticationFilter;
import com.dorandoran.doranserver.config.jwt.TokenProvider;
import com.dorandoran.doranserver.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeRequests()
                .mvcMatchers("/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**").permitAll(); //swagger 3 관련 Url 접근 허가

        httpSecurity.authorizeRequests()
                        .antMatchers("/api/check-nickname").permitAll()
                        .antMatchers("/api/check/registered").permitAll()
                        .antMatchers("/api/signup").permitAll()
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().permitAll();

        httpSecurity.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable(); //Jwt authorization 을 위한 formLogin 비활성화

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Jwt authorization 을 위한 session 비활성화

        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //Authentication Header 확인하는 커스텀 필터 등록

        httpSecurity.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**"));

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


