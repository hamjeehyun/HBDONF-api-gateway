package com.teamDevfuse.hbdonf.apigateway.config;

import com.google.firebase.auth.FirebaseAuth;
import com.teamDevfuse.hbdonf.apigateway.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    @Autowired
    private FirebaseAuth firebaseAuth;

    private static final String[] RERMIT_URL_ARRAY = {
            "/hbdonf-jswtt/v2/api-docs"
            ,"/hbdonf-jswtt-dev/**"
            ,"/hbdonf-api-gateway/v2/api-docs"
            ,"/hbdonf-api-gateway-dev/v2/api-docs"
            ,"/hbdonf-api-gateway-dev/token/**"
            ,"/hbdonf-api-gateway-dev/test"
            ,"/hbdonf-jswtt-score/v2/api-docs"
            ,"/hbdonf-token/**"
            ,"/v2/api-docs"
            ,"/swagger-resources"
            ,"/swagger-resources/**"
            ,"configuration/ui"
            ,"configuration/security"
            ,"/swagger-ui.html"
            ,"/webjars/**"
            ,"/swagger/**"
            ,"/swagger-ui/**"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .addFilterBefore(new JwtFilter(firebaseAuth),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //spring security 제외 경로설정
        web.ignoring()
                .antMatchers(HttpMethod.GET, "/hbdonf-jswtt/message")
                .antMatchers(HttpMethod.POST, "/hbdonf-jswtt/user")
                .antMatchers(HttpMethod.GET, "/hbdonf-api-gateway/token/**")
                .antMatchers(RERMIT_URL_ARRAY);

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT");
    }

}