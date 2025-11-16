package com.ticonsys.online_meet.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService {
    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
