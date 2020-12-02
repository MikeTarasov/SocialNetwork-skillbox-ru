package ru.skillbox.socialnetwork.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider {

  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public AuthenticationProvider(
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
  }

  public String getAuthentication(String email, String pass) throws Exception {
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(email, pass));
    return jwtTokenProvider.generateToken(email);
  }
}
