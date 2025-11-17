package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.BaseRequest;
import com.ticonsys.online_meet.dto.Response;
import com.ticonsys.online_meet.dto.UserRequest;
import com.ticonsys.online_meet.dto.UserResponse;
import com.ticonsys.online_meet.enums.ApiMessage;
import com.ticonsys.online_meet.model.user.Permission;
import com.ticonsys.online_meet.model.user.Role;
import com.ticonsys.online_meet.model.user.User;
import com.ticonsys.online_meet.repo.UserRepository;
import com.ticonsys.online_meet.utils.CommonUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public <D extends BaseRequest> ResponseEntity<Response<?>> getAllData(D filterData) {
        return null;
    }

    @Override
    public ResponseEntity<Response<?>> getDataById(Long id) {
        return null;
    }

    @Override
    public <D extends BaseRequest> ResponseEntity<Response<?>> createData(D data) {
        UserRequest userRequest = (UserRequest) data;
        System.err.println(userRequest.toString());
        return null;
    }

    @Override
    public <D extends BaseRequest> ResponseEntity<Response<?>> updateData(Long id, D data) {
        return null;
    }

    @Override
    public ResponseEntity<Response<?>> deleteData(Long id) {
        return null;
    }

    @Override
    public List<GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Role role = user.getRole();
        if(role == null || role.getIsDeleted() )
            return authorities;
        List<Permission> permissions = role.getPermissions();

        for (Permission permission : permissions) {
            if (permission.getAction() != null && !permission.getAction().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(permission.getAction()));
            }
        }
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return authorities;
    }

    @Transactional(readOnly = true)
    @Override
    public Role findRoleByUserIdAndIsDeletedFalse(Long userId) {
        return userRepository.findRoleByUserIdAndIsDeletedFalse(userId).orElseThrow(() -> new IllegalArgumentException(CommonUtil.getApiMessages(ApiMessage.ROLE_NOT_EXIST)));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(CommonUtil.getApiMessages( ApiMessage.USER_NOT_EXIST)));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new UsernameNotFoundException(CommonUtil.getApiMessages( ApiMessage.USER_NOT_EXIST)));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User getReference(Long id) {
        return entityManager.getReference(User.class, id);
    }

    @Override
    public User findByUsernameAndIsActiveTrueAndIsDeletedFalse(String username) {
        return userRepository.findByUsernameAndIsActiveTrueAndIsDeletedFalse(username).orElseThrow(() -> new IllegalArgumentException(CommonUtil.getApiMessages( ApiMessage.USER_NOT_EXIST)));
    }

    @Override
    public UserResponse getBasicUserResponse(User user ) {

        if ( user == null )
            return null;

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        return userResponse;
    }
}
