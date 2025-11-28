package com.fth.user.security;

import com.fth.user.model.AppUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final Map<String, AppUser> users = new HashMap<>();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        // sample users (passwords encoded)
        // passenger: passenger1 / pass123
        // driver: driver1 / driver123
        users.put("passenger1", new AppUser("passenger1", encoder.encode("pass123"), List.of("PASSENGER")));
        users.put("driver1", new AppUser("driver1", encoder.encode("driver123"), List.of("DRIVER")));

        // optional: a user with both roles
        users.put("both1", new AppUser("both1", encoder.encode("both123"), List.of("PASSENGER", "DRIVER")));
    }

    public Optional<AppUser> findAppUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = users.get(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String r : appUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
        }
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }

    // helper: validate role membership
    public boolean userHasRole(String username, String role) {
        AppUser u = users.get(username);
        if (u == null) return false;
        return u.getRoles().contains(role);
    }
}
