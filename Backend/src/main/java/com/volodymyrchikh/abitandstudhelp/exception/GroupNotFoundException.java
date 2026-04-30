package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class GroupNotFoundException extends RuntimeException {

    private final String groupName;

    public GroupNotFoundException(String message, String groupName) {
        super(message);
        this.groupName = groupName;
    }

}
