package com.ticonsys.online_meet.repo;

import com.ticonsys.online_meet.model.user.Role;
import com.ticonsys.online_meet.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByUsernameAndIsActiveTrueAndIsDeletedFalse(String name);

    Optional<User> findByUsername(String name);
    List<User> findAllByIdIn(Collection<Long> ids);

    Optional<User> findById( Long userId );

    @Query("select u.id, u.profileName from User u " +
            "join u.role r " +
            "where u.id in :userIds")
    List<Object[]> findAllCompaniesByIdInAndIsDeletedFalse( Collection<Long> userIds );

    boolean existsByUsername( String username );

    @Query("select r from User u " +
            "join u.role r " +
            "where u.isDeleted = false and r.isDeleted = false and u.id = :userId")
    Optional<Role> findRoleByUserIdAndIsDeletedFalse(Long userId );

}
