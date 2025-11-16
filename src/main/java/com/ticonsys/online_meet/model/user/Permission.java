package com.ticonsys.online_meet.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    private Long parent;

    private Long sort;

    private Boolean isActive;

    @Column(length = 100)
    private String action;

    @Column(length = 100)
    private String menuRoute;

    @Enumerated(EnumType.STRING)
    private MenuType menuType;

    public Permission(Long id, String name, Long parent, String isActive, String action) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.sort = id;
        this.isActive = true;
        this.action = action;
    }

    public Permission(Long id, Long sort, String action) {
        this.id = id;
        this.sort = sort;
        this.action = action;
    }
}
