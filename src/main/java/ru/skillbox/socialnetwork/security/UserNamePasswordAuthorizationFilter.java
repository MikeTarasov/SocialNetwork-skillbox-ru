package ru.skillbox.socialnetwork.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.skillbox.socialnetwork.api.responses.ErrorErrorDescriptionResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.repository.PersonRepository;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


class UserNamePasswordAuthorizationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final Logger logger;
    private final String jwtHeader;
    private final String jwtPrefix;


    public UserNamePasswordAuthorizationFilter(PersonRepository personRepository,
                                               JwtTokenProvider jwtProvider,
                                               String jwtHeader,
                                               String jwtPrefix) {
        this.personRepository = personRepository;
        this.jwtProvider = jwtProvider;
        this.jwtHeader = jwtHeader;
        this.jwtPrefix = jwtPrefix;

        logger = LogManager.getRootLogger();
        authenticationDetailsSource = new WebAuthenticationDetailsSource();
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/auth/login", "POST"));
        objectMapper = new ObjectMapper();
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String email = getParameter(request, "email");
        String password = getParameter(request, "password");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authRequest);

        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getParameter(HttpServletRequest request, String name) {
        String result = request.getParameter(name);
        result = result != null ? result : "";
        return result.trim();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        String email = new ObjectMapper().readValue(request.getInputStream(), PersonDetails.class).getEmail();

        Optional<Person> optionalPerson = personRepository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            String token = jwtProvider.generateToken(email);
            response.addHeader(jwtHeader, token);

            Person person = optionalPerson.get();
            if (person.isBlocked() || person.isDeleted()) {
                errorResponse(String.format("user block(%s) or deleted(%s)!", person.isBlocked(), person.isDeleted()),
                        response, HttpStatus.BAD_REQUEST);
            } else {
                successResponse(person, response, token);
            }
        } else {
            errorResponse("This email is not registered", response, HttpStatus.BAD_REQUEST);
        }
    }

    private void errorResponse(String message, HttpServletResponse response, HttpStatus httpStatus) throws IOException {
        ErrorErrorDescriptionResponse errorApi = new ErrorErrorDescriptionResponse(message);
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(errorApi));
        response.setStatus(httpStatus.value());
    }

    private void successResponse(Person person, HttpServletResponse response, String token) throws IOException {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

            ErrorTimeDataResponse dataResponse = new ErrorTimeDataResponse("",
                    new PersonEntityResponse(
                            person.getId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getRegDate(),
                            person.getBirthDate(),
                            person.getEmail(),
                            person.getPhone(),
                            person.getPhoto(),
                            person.getAbout(),
                            person.getCity(),
                            person.getCountry(),
                            person.getMessagePermission(),
                            person.getLastOnlineTime(),
                            person.isBlocked(),
                            token
                    ));
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getOutputStream(), dataResponse);
        } catch (Exception e) {
            logger.error(e);
            errorResponse("Invalid authorization", response, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        errorResponse("Un authorized", response, HttpStatus.UNAUTHORIZED);
    }
}