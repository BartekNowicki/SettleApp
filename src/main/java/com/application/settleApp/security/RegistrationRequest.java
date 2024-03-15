package com.application.settleApp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@AllArgsConstructor
//@Getter
//public class RegistrationRequest extends AuthRequest {}


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String email;
    private String password;
}