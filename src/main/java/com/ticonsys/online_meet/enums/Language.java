package com.ticonsys.online_meet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language implements NamedConstant {
    EN("English", "EN");

    private final String name;
    private final String key;
}
