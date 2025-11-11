package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishStreamDto {
    private String roomId;
    private String clientId;
    private String type; // offer (video, audio info), answer (Accept offer & confirm settings), ice (Share network candidates to connect peers)
    private JSONObject jsep;
}

