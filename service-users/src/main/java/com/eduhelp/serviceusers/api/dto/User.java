package com.eduhelp.serviceusers.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class User {
    private String username;
    private String name;
    private String surname;
}


