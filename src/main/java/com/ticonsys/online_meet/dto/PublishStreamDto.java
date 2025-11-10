package com.ticonsys.online_meet.dto;

import lombok.Data;

@Data
public class PublishStreamDto {
    private String roomId;
    private String clientId;
    private String type; // offer (video, audio info), answer (Accept offer & confirm settings), ice (Share network candidates to connect peers)
    private String sdp;
}

