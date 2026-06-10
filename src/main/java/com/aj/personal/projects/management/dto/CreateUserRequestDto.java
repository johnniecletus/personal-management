package com.aj.personal.projects.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {
    private String email;
    private String fullName;
    private String userName;
    private String password;
}
