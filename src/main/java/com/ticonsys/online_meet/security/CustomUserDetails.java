package com.ticonsys.online_meet.security;

import com.ticonsys.online_meet.enums.Language;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

//@Builder
@Data
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private Long userId;
    private Long roleId;
    private Language language;
    private Collection<? extends GrantedAuthority> authorities;

    @Builder
    public CustomUserDetails(String username, String password, Long userId, Long roleId, Language language, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.roleId = roleId;
        this.language = language;
        this.authorities = authorities;
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

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
