package com.ticonsys.online_meet.dto;

import lombok.Data;

@Data
public class PublishStreamDto {
    private String roomId;
    private String displayName;
    private String sdpOffer;
}

