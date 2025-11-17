package com.ticonsys.online_meet.utils;

public interface ApiUri {
    String BASE_URI = "/api/v1";
    String BASE_URI_USERS = BASE_URI + "/users";
    String BASE_URI_AUTH = BASE_URI + "/auth";
    String BASE_URI_ACTIVITY_LOG = BASE_URI + "/activity-log";
    String BASE_URI_NOTIFICATION = BASE_URI + "/notification";
    String NOTIFY_SENDER =  "/topic/notify-send";
    String NOTIFY_RECEIVED =  "/notify-received";
}
