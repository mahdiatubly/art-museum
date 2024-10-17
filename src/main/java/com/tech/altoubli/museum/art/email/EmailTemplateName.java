package com.tech.altoubli.museum.art.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    RESET_FORGOTTEN_PASSWORD("reset_forgotten_password"),
    FOLLOWING_REQUEST("following_request");


    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
