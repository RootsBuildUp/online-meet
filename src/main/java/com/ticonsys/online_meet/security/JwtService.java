package com.ticonsys.online_meet.security;

import com.ticonsys.online_meet.model.user.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String extractUserName(String token);
    String generateToken(User user);
    boolean isTokenValid(String token, UserDetails userDetails);
    ResponseCookie generateJwtCookie(String jwt);
    String getJwtFromCookies(HttpServletRequest request);
    ResponseCookie getCleanJwtCookie();
    Claims extractAllClaims(String token);
    Date extractExpiration(String token);
    String getJwt(HttpServletRequest request);
}
