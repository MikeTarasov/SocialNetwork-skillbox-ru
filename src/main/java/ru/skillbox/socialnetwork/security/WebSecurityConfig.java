package ru.skillbox.socialnetwork.security;


import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.skillbox.socialnetwork.repository.PersonRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtProvider;
    private final UserNamePasswordAuthorizationFilter userNamePasswordAuthorizationFilter;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final JwtConfig jwtConfig;

    public WebSecurityConfig(PersonDetailsService personDetailsService,
                             PersonRepository personRepository,
                             JwtTokenProvider jwtProvider, JwtConfig jwtConfig) {
        this.personDetailsService = personDetailsService;
        this.personRepository = personRepository;
        this.jwtProvider = jwtProvider;
        this.jwtConfig = jwtConfig;
        this.userNamePasswordAuthorizationFilter = new UserNamePasswordAuthorizationFilter(
                personRepository, jwtProvider, jwtConfig.getJwtHeader(), jwtConfig.getJwtPrefix());
        this.jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter(
                jwtProvider, personDetailsService, jwtConfig.getJwtHeader(), jwtConfig.getJwtPrefix());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers("/account/register", "/account/password/recovery", "/account/password/set",
                        "/platform/**", "/api/test/**"
//                        , "/*/**"     //раскомментирование отключает security
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenAuthenticationFilter, UserNamePasswordAuthorizationFilter.class)
                .addFilterAfter(userNamePasswordAuthorizationFilter, JwtTokenAuthenticationFilter.class)
                .formLogin()
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.ACCEPTED))
                .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService).passwordEncoder(bCryptEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}