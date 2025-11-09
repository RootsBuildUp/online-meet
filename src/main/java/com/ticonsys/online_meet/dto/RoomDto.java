package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    private String roomId;
    @NonNull
    private String name;
    @NonNull
    private int participants;
    @NonNull
    private String adminName;
    private long janusRoomId;


}
