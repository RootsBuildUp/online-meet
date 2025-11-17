package com.ticonsys.online_meet.controller;

import com.ticonsys.online_meet.dto.PermissionResponse;
import com.ticonsys.online_meet.dto.Response;
import com.ticonsys.online_meet.dto.UserRequest;
import com.ticonsys.online_meet.service.PermissionEvaluatorService;
import com.ticonsys.online_meet.service.PermissionService;
import com.ticonsys.online_meet.service.UserService;
import com.ticonsys.online_meet.utils.ApiUri;
import com.ticonsys.online_meet.utils.Constant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = ApiUri.BASE_URI_USERS)
public class UserController extends BaseController<UserService, UserRequest> {

    private final UserService service;
    public static final String PERMISSION_USERS = "USER";
    private final PermissionEvaluatorService permissionEvaluatorService;
    private final PermissionService permissionService;

    public UserController(UserService service, PermissionEvaluatorService permissionEvaluatorService, PermissionService permissionService) {
        super(service,PERMISSION_USERS, permissionEvaluatorService);
        this.service = service;
        this.permissionEvaluatorService = permissionEvaluatorService;
        this.permissionService = permissionService;
    }

    // GET ALL
    @GetMapping("/menu-privilege")
    @Transactional(readOnly = true)
    public ResponseEntity<Response<List<PermissionResponse>>> getMenuPrivilege() {
        return ResponseEntity.ok(new Response<>(permissionService.getPermissionsForCurrentUser()));
    }

}
