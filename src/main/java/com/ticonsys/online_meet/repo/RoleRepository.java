package com.ticonsys.online_meet.repo;

import com.ticonsys.online_meet.model.user.Permission;
import com.ticonsys.online_meet.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findFirstByNameAndIsDeletedFalse(String name);

    @Query("select r.permissions from Role r " +
            "where r = :role")
    List<Permission> findAllPermissionsByRole(Role role);

}
