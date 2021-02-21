package ru.skillbox.socialnetwork.security;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final PersonDetailsService personDetailsService;
    private final String jwtHeader;
    private final String jwtPrefix;

    public JwtTokenAuthenticationFilter(JwtTokenProvider jwtProvider, PersonDetailsService personDetailsService, String jwtHeader, String jwtPrefix) {
        this.jwtProvider = jwtProvider;
        this.personDetailsService = personDetailsService;
        this.jwtHeader = jwtHeader;
        this.jwtPrefix = jwtPrefix;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (httpServletRequest.getHeader(jwtHeader) == null /* || !header.startsWith(jwtConfig.getPrefix()) */) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);   // If not valid, go to the next filter.
            return;
        }

        String token = getTokenFromRequest(httpServletRequest);

        if (token != null && jwtProvider.validateToken(token)) {

            String userLogin = jwtProvider.getLoginFromToken(token);

            PersonDetails personDetails = personDetailsService.loadUserByUsername(userLogin);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    personDetails, null, personDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
            personDetailsService.updateLastOnline(personDetails.getUsername());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(jwtHeader);
        if (StringUtils.hasText(bearer) && bearer.startsWith(jwtPrefix)) {
            return bearer.substring(7);
        }
        return null;
    }
}