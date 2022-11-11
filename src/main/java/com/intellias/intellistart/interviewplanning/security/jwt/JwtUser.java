package com.intellias.intellistart.interviewplanning.security.jwt;

import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * JWT implementation of User model for Spring Security.
 */
public class JwtUser implements UserDetails {

  private final String name;
  private final String email;
  private final UserRole userRole;
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * JwtUser constructor for users that are present in database.
   *
   * @param name        User's name
   * @param email       User's email
   * @param userRole    User's role
   * @param authorities Authorities based on role to limit access to endpoints
   */
  public JwtUser(String name, String email, UserRole userRole,
      Collection<? extends GrantedAuthority> authorities) {
    this.name = name;
    this.email = email;
    this.userRole = userRole;
    this.authorities = authorities;
  }

  /**
   * JwtUser constructor for users that are not present in database.
   *
   * @param name  User's name
   * @param email User's email
   */
  public JwtUser(String name, String email) {
    this.name = name;
    this.email = email;
    this.userRole = UserRole.CANDIDATE;
    this.authorities = List.of(new SimpleGrantedAuthority(userRole.toString()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
