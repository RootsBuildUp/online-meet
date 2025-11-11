package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinUserDto {
    private String roomId;
    private String clientId;
    private String displayName;
}
