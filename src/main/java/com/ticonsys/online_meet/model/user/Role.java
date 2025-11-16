package com.ticonsys.online_meet.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.ticonsys.online_meet.dto.JsonViews;
import com.ticonsys.online_meet.model.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView( JsonViews.Small.class)
    private Long id;

    @Column(length = 60)
    @JsonView( JsonViews.Small.class)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions = new ArrayList<>();

    @Column(length = 100)
    private String note;

    @Transient
    private List<Long> permittedList;

    public List<Long> getPermittedList() {
        if( permissions != null )
            return permissions.stream().map( Permission::getId ).toList();
        else return new ArrayList<>();
    }
}
