package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.AuthenticationRequest;
import com.ticonsys.online_meet.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);

}
