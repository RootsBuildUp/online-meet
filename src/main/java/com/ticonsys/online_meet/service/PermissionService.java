package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.PermissionResponse;
import com.ticonsys.online_meet.model.user.Permission;
import com.ticonsys.online_meet.model.user.Role;

import java.util.List;

public interface PermissionService {

    List<Permission> getPermissionsOfCurrentUser();
    List<PermissionResponse> getPermissionsForCurrentUser();

    List<PermissionResponse> getAssignablePermissions();

    List<PermissionResponse> getPermissionsWithHierarchy(Long userId);
    List<PermissionResponse> getPermissionsWithHierarchyByRole(Role role);

    List<Permission> findAll();

    List<Permission> findByIdIn(List<Long> permissionArray);

    List<Permission> findAllById(List<Long> permissionIds);

    List<Permission> findByParent(Long id);
}
