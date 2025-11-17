package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.AuthenticationRequest;
import com.ticonsys.online_meet.dto.AuthenticationResponse;
import com.ticonsys.online_meet.dto.Response;
import com.ticonsys.online_meet.enums.ApiMessage;
import com.ticonsys.online_meet.security.JwtService;
import com.ticonsys.online_meet.service.AuthenticationService;
import com.ticonsys.online_meet.utils.ApiUri;
import com.ticonsys.online_meet.utils.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = ApiUri.BASE_URI_AUTH)
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<Response<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
        System.err.println(request.toString());
        var response = authenticationService.authenticate(request);
        return ResponseEntity.ok(new Response<>(response));
    }

    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout() {
        String jwtToken = jwtService.getJwt( httpServletRequest );
        if (jwtToken != null) {
            Date expirationDate = jwtService.extractExpiration( jwtToken );
        }
        return ResponseEntity.ok(new Response<>(CommonUtil.getApiMessages(ApiMessage.LOGOUT)));
    }


}
