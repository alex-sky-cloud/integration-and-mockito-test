package com.example.testingwithmockmvc.blog;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests(authorize -> authorize
                        .mvcMatchers(HttpMethod.GET, "/dashboard").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                        .mvcMatchers("/api/users/**").permitAll()
                        .mvcMatchers("/**").authenticated()
                )
                .csrf()
                .and()
                .httpBasic()
                .and()
                .build();
    }
}
