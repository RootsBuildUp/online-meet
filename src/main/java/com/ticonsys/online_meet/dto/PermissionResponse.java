package com.ticonsys.online_meet.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PermissionResponse {
    private Long id;
    private String name;
    private String action;
    private String menuRoute;
    private String menuType;
    private Long sort;
    private List<PermissionResponse> children;
}
