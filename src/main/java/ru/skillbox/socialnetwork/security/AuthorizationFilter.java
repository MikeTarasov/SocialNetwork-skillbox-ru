package ru.skillbox.socialnetwork.security;

import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AuthorizationFilter extends GenericFilterBean {

  @Value("${jwt.token.header}")
  private String jwtHeader;

  @Value("${jwt.token.prefix}")
  private String jwtPrefix;

  private JwtTokenProvider jwtProvider;
  private PersonDetailsService personDetailsService;

  public AuthorizationFilter(JwtTokenProvider jwtProvider,
      PersonDetailsService personDetailsService) {
    this.jwtProvider = jwtProvider;
    this.personDetailsService = personDetailsService;
  }




  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    String token = getTokenFromRequest((HttpServletRequest) servletRequest);
    if (token != null && jwtProvider.validateToken(token)) {
      String userLogin = jwtProvider.getLoginFromToken(token);
      PersonDetails personDetails = personDetailsService.loadUserByUsername(userLogin);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          personDetails, null, personDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    filterChain.doFilter(servletRequest, servletResponse);

  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String bearer = request.getHeader(jwtHeader);
    if (hasText(bearer) && bearer.startsWith(jwtPrefix)) {
      return bearer.substring(7);
    }
    return null;
  }

}
