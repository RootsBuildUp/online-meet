package com.ticonsys.online_meet.config;

import com.ticonsys.online_meet.enums.ApiMessage;
import com.ticonsys.online_meet.security.CustomUserDetails;
import com.ticonsys.online_meet.security.CustomUserDetailsService;
import com.ticonsys.online_meet.security.JwtService;
import com.ticonsys.online_meet.utils.CommonUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.err.println(request.getRequestURL());
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String token = jwtService.getJwt(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String userName = jwtService.extractUserName(token);
            System.err.println(userName);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userName != null && authentication == null) {
                CustomUserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userName);
                if (!jwtService.isTokenValid(token, userDetails))
                    throw new JwtException(ApiMessage.INVALID_TOKEN.getEn());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(CommonUtil.getApiMessages(ApiMessage.INVALID_TOKEN));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
