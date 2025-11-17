package com.ticonsys.online_meet.service;

import com.ticonsys.online_meet.dto.PermissionResponse;
import com.ticonsys.online_meet.model.user.MenuType;
import com.ticonsys.online_meet.model.user.Permission;
import com.ticonsys.online_meet.model.user.Role;
import com.ticonsys.online_meet.repo.PermissionRepository;
import com.ticonsys.online_meet.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImp implements PermissionService {

    private final PermissionRepository permissionRepository;
    private UserService userService;

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<Permission> getPermissionsOfCurrentUser() {
        Long userId = CommonUtil.getUserIdFromSecurityContext();
        var currentUser = userService.getUserById(userId);
        var role = currentUser.getRole();
        var permissions = role.getPermissions();
        return permissions;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> getPermissionsForCurrentUser() {
        Long userId = CommonUtil.getUserIdFromSecurityContext();
        return getPermissionsWithHierarchy(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> getAssignablePermissions() {
        List<Permission> permissions = permissionRepository
                .findAllByIsActive(true).stream()
                .toList();

        return buildPermissionHierarchy(permissions,null);

    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> getPermissionsWithHierarchy(Long userId) {
        Role role = userService.findRoleByUserIdAndIsDeletedFalse(userId);
        return getPermissionsWithHierarchyByRole(role);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsWithHierarchyByRole(Role role) {

        if (role.getIsDeleted())
            return new ArrayList<>();

        // Step 1: Load all active permissions for this role
        List<Permission> permissions = role.getPermissions();

        return buildPermissionHierarchy(permissions, null);
    }

    private List<PermissionResponse> buildPermissionHierarchy(List<Permission> allPermissions, Long parentId) {
        return allPermissions.stream()
                .filter(p -> Objects.equals(p.getParent(), parentId))
                .map(p -> {
                    PermissionResponse response = CommonUtil.copyProperties(p, new PermissionResponse());
                    if (!p.getMenuType().equals(MenuType.PERMISSION))
                        response.setChildren(buildPermissionHierarchy(allPermissions, p.getId()));
                    return response;
                })
                .sorted(Comparator.comparing(PermissionResponse::getSort))
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> findByIdIn(List<Long> permissionArray) {
        List<Permission> childrenList = permissionRepository.findByIdInAndIsActiveAndParentNullOrderBySortAsc(permissionArray, true);

        Set<Long> parentIdList = new HashSet<>();
        childrenList.forEach(p -> parentIdList.add(p.getParent()));

        List<Permission> parentList = permissionRepository.findByIdInAndIsActiveOrderBySortAsc(parentIdList.stream().toList(), true);

        List<Permission> permissionList = new ArrayList<>();
        permissionList.addAll(parentList);
        permissionList.addAll(childrenList);
        return permissionList;
    }

    @Override
    public List<Permission> findAllById(List<Long> permissionIds) {
        return permissionRepository.findAllById(permissionIds);
    }

    @Override
    public List<Permission> findByParent(Long id) {
        return permissionRepository.findByParent(id);
    }
}
