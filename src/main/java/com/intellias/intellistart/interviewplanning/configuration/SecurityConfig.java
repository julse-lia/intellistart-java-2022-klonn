package com.intellias.intellistart.interviewplanning.configuration;

import com.intellias.intellistart.interviewplanning.exceptions.ControllerAdvisor;
import com.intellias.intellistart.interviewplanning.security.JwtConfigurer;
import com.intellias.intellistart.interviewplanning.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Configuration class of Spring Security.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final JwtTokenProvider jwtTokenProvider;

  //COORDINATOR endpoints
  private static final String COORDINATOR = "COORDINATOR";
  private static final String DASHBOARD_ENDPOINT = "/weeks/{weekId}/dashboard";
  private static final String UPD_INTER_SLOT_ENDPOINT =
      "/interviewers/{interviewerId}/slots/{slotId}";
  private static final String BOOKINGS_ENDPOINTS = "/bookings/**";
  private static final String USERS_ENDPOINTS = "/users/**";

  //INTERVIEWER endpoints
  private static final String INTERVIEWER = "INTERVIEWER";
  private static final String CREATE_UPD_SLOT_ENDPOINTS = "/interviewers/**";
  private static final String GET_CUR_WEEK_SLOTS_ENDPOINT = "/weeks/current/interviewers/**";
  private static final String GET_NEXT_WEEK_SLOTS_ENDPOINT = "/weeks/next/interviewers/**";

  //CANDIDATE endpoints
  private static final String CANDIDATE = "CANDIDATE";
  private static final String CANDIDATES_ENDPOINTS = "/candidates/**";

  //Login endpoint
  private static final String LOGIN_ENDPOINT = "/authenticate";

  @Autowired
  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  //  @Bean
  //  @Override
  //  public AuthenticationManager authenticationManagerBean() throws Exception {
  //    return super.authenticationManagerBean();
  //  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(LOGIN_ENDPOINT).permitAll()
        .antMatchers(DASHBOARD_ENDPOINT).hasAuthority(COORDINATOR)
        .antMatchers(UPD_INTER_SLOT_ENDPOINT).hasAuthority(COORDINATOR)
        .antMatchers(BOOKINGS_ENDPOINTS).hasAuthority(COORDINATOR)
        .antMatchers(USERS_ENDPOINTS).hasAuthority(COORDINATOR)
        .antMatchers(CREATE_UPD_SLOT_ENDPOINTS).hasAuthority(INTERVIEWER)
        .antMatchers(GET_CUR_WEEK_SLOTS_ENDPOINT).hasAuthority(INTERVIEWER)
        .antMatchers(GET_NEXT_WEEK_SLOTS_ENDPOINT).hasAuthority(INTERVIEWER)
        .antMatchers(CANDIDATES_ENDPOINTS).hasAuthority(CANDIDATE)
        .anyRequest().authenticated()
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().exceptionHandling()
        .authenticationEntryPoint(new ControllerAdvisor.JwtAuthenticationEntryPoint())
        .and().exceptionHandling()
        .accessDeniedHandler(new ControllerAdvisor.CustomAccessDeniedHandler());
    http.apply(new JwtConfigurer(jwtTokenProvider));
  }

}