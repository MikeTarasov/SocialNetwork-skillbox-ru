package ru.skillbox.socialnetwork.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skillbox.socialnetwork.model.entities.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDetails implements UserDetails {

    private String username;
    private String password;
    private List<SimpleGrantedAuthority> authorities;
    private String email;


    public PersonDetails(String username, String password,
                         List<SimpleGrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.authorities = getSimpleGrantedAuthorities();
    }

    public static PersonDetails fromUser(Person per) {
        return new PersonDetails(per.getEmail(), per.getPassword(), getSimpleGrantedAuthorities());
    }

    private static List<SimpleGrantedAuthority> getSimpleGrantedAuthorities() {
        List<SimpleGrantedAuthority> listAuth = new ArrayList<>();
        listAuth.add(new SimpleGrantedAuthority("ROLE_USER"));
        return listAuth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}