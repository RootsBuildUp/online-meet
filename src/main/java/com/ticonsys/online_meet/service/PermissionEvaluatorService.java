package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionEvaluatorService {

    public boolean hasPermission(String permissionPrefix, String... actions) {

        List<String> actionList = Arrays.stream(actions).map(action -> permissionPrefix + "_" + action).collect(Collectors.toList());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;
        actionList.add("SUPPER_ADMIN");

        return auth.getAuthorities().stream()
                .anyMatch(granted -> actionList.contains(granted.getAuthority()));
    }

    public void checkPermission(String permissionPrefix, String... actions) {
        if (!hasPermission(permissionPrefix, actions))
            throw new AccessDeniedException("Access denied");
    }

}
