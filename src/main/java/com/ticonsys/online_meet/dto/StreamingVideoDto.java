package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamingVideoDto {

    private VideoType type;
    private String roomId;
    private String clientId;
    private String displayName;
    private String sdp;

    // Overloaded constructor (without roomId)
    public StreamingVideoDto(VideoType type, String clientId, String displayName, String sdp) {
        this(type, null, clientId, displayName, sdp);
    }

    // Enum
    public enum VideoType {
        join,
        answer,
        offer,
        ice,
        leave
    }
}
