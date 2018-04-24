package com.twobomb.app.security;

import com.twobomb.entity.User;
import com.twobomb.repository.UserRepository;
import com.twobomb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Qualifier("UserService")
    @Autowired
        UserService us;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = us.getByLogin(username);
        if (null == user) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin().toLowerCase(),
                    user.getPassword(),
                    user.getAuthorities()
            );

        }
    }
}
