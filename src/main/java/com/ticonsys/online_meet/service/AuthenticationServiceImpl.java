package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.AuthenticationRequest;
import com.ticonsys.online_meet.dto.AuthenticationResponse;
import com.ticonsys.online_meet.model.user.User;
import com.ticonsys.online_meet.security.JwtService;
import com.ticonsys.online_meet.utils.Constant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;
    private final PermissionService permissionService;


    @Transactional
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userService.findByUsernameAndIsActiveTrueAndIsDeletedFalse(request.username());
        var jwt = jwtService.generateToken(user);
        return authenticate(user, jwt);
    }

    private AuthenticationResponse authenticate(User user, String jwt) {
        var permissionList = permissionService.getPermissionsWithHierarchy(user.getId());

        return new AuthenticationResponse(Constant.TOKEN_PREFIX.trim(), jwt);
    }
}
