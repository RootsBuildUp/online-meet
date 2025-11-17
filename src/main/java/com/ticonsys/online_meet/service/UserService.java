package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.UserResponse;
import com.ticonsys.online_meet.model.user.Role;
import com.ticonsys.online_meet.model.user.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface UserService extends BaseService {
    UserResponse getBasicUserResponse(User user);
    List<GrantedAuthority> getAuthorities(User user);
    User findByUsernameAndIsActiveTrueAndIsDeletedFalse(String username);
    Role findRoleByUserIdAndIsDeletedFalse(Long userId);
    User getUserById(Long userId);
    User save(User user);
    User findByEmail(String email);
    boolean existsByUsername( String username );
    User getReference( Long id );
}
