package com.ticonsys.online_meet.repo;

import com.ticonsys.online_meet.model.user.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByIdInAndIsActiveAndParentNotNullOrderBySortAsc(List<Long> permissions, boolean active);
    List<Permission> findByIdInAndIsActiveAndParentNullOrderBySortAsc(List<Long> permissions, boolean active);
    List<Permission> findAllByIsActive(boolean active);

    List<Permission> findByIdInAndIsActiveOrderBySortAsc(List<Long> permissions, boolean active);

    List<Permission> findByParent(Long id);
}
