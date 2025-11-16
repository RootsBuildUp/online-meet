package com.ticonsys.online_meet.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticonsys.online_meet.enums.Language;
import com.ticonsys.online_meet.model.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String username;

    @Column(length = 100)
    @JsonIgnore
    private String password;

    @Column
    private String profileName;

    @Column
    private String gender;

    @Column(length = 60)
    private String email;

    @Column(length = 20)
    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Language language = Language.EN; // e.g., 'EN'

    private Boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "active_role_id")
    @JsonIgnore
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (role != null && role.getPermissions() != null) {
            authorities.addAll(role.getPermissions().stream()
                    .filter(permission -> permission.getAction() != null)
                    .map(permission -> new SimpleGrantedAuthority(permission.getAction()))
                    .toList());

            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profileName='" + profileName + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", language=" + language +
                ", isActive=" + isActive +
                '}';
    }
}
