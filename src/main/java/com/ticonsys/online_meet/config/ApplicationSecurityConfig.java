package com.ticonsys.online_meet.config;

import com.ticonsys.online_meet.model.user.User;
import com.ticonsys.online_meet.repo.UserRepository;
import com.ticonsys.online_meet.security.CustomUserDetails;
import com.ticonsys.online_meet.security.CustomUserDetailsService;
import com.ticonsys.online_meet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationSecurityConfig {
    private final UserRepository userRepository;
    private UserService userService;

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    @Primary
    public CustomUserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsernameAndIsActiveTrueAndIsDeletedFalse(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

           var authorities =  userService.getAuthorities(user);
           var role = user.getRole();

            return CustomUserDetails
                    .builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roleId(role.getId())
                    .language(user.getLanguage())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    @Primary
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
