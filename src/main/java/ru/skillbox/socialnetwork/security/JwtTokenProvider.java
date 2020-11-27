package ru.skillbox.socialnetwork.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final int MILIS_IN_SEC = 1000;
  private final int SEC_IN_MIN = 60;

  @Value("${jwt.token.secret-key}")
  private String jwtSecret;

  @Value("${jwt.token.exp-time-in-min}")
  private int jwtExpTime;

  @Value("${jwt.token.prefix}")
  private String jwtPrefix;

  public String generateToken(String email) {
    Date exp = new Date(
        System.currentTimeMillis() + (jwtExpTime * MILIS_IN_SEC
            * SEC_IN_MIN));
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    Claims claims = Jwts.claims().setSubject(email);
    return jwtPrefix + Jwts.builder()
        .setClaims(claims)
        .signWith(key, SignatureAlgorithm.HS512)
        .setExpiration(exp)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException expEx) {
      System.out.println("Token expired");
    } catch (UnsupportedJwtException unsEx) {
      System.out.println("Unsupported jwt");
    } catch (MalformedJwtException mjEx) {
      System.out.println("Malformed jwt");
    } catch (SignatureException sEx) {
      System.out.println("Invalid signature");
    } catch (Exception e) {
      System.out.println("invalid token");
    }
    return false;
  }

  public String getLoginFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

}
