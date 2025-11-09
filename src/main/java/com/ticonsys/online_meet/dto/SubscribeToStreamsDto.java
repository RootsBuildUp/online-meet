package com.ticonsys.online_meet.dto;

import lombok.Data;

@Data
public class SubscribeToStreamsDto {
    private String roomId;
    private String sdpOffer;
}
