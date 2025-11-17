package com.ticonsys.online_meet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiMessage {

    SUCCESSFUL_ADD("Successfully added.", ""),
    SUCCESSFUL_UPDATE("Successfully updated.", ""),
    SUCCESSFUL_UPLOAD("Successfully upload.", ""),
    SUCCESSFUL_DEACTIVATE("Successfully deactivated.", ""),
    SUCCESSFUL_DELETED("Successfully deleted.", ""),

    ROLE_NOT_EXIST("Role not found.", ""),

    USER_NOT_EXIST("User not found.", ""),
    USER_EXIST("User already exists.", ""),
    USER_ACCESS_DENIED("User Access Denied.", ""),
    USER_DO_NOT_DELETE(" company admin and supper admin do not deleted.", ""),



    WAITING_FOR_APPROVED("Waiting for approved.", ""),

    MATERIAL_DO_NOT_UPLOAD("Material do not upload.", ""),
    MATERIAL_PATH_INVALID("Invalid file path.", ""),
    MATERIAL_NOT_FOUND("Material not found.", ""),
    MATERIAL_USED("This material used in template.", ""),


    LOGOUT("Logged out successfully.", ""),
    LOGOUT_DO_NOT("Logged out unsuccessful.", ""),

    ERROR_SECURITY_CONTEXT("security context error while fetching user id from security context.", ""),
    ERROR("Error occurred while processing request.", ""),

    PASSWORD_DONT_MATCH("Password and Confirm Password does not match.", ""),
    PASSWORD_WRONG("Current password do not match.", ""),
    PASSWORD_RESET_SENT("If an account with that email exists, a reset email has been sent.", ""),
    PASSWORD_RESET("Password reset successfully.", ""),
    PASSWORD_CHANGE("Successfully password change.", ""),
    PASSWORD_EMAIL_WRONG("Email or Password does not match.", ""),
    REQUIRED_USER_ID("User id required.", ""),

    TOKEN_EXPIRED("Invalid or expired token.", ""),
    INVALID_TOKEN("Invalid token.", ""),
    UNAUTHORIZED_USER("Unauthorized user.", ""),
    ;

    private final String en;
    private final String bn;
}
