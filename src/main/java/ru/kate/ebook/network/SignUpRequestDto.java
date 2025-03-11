package ru.kate.ebook.network;

import lombok.Data;

@Data

public class SignUpRequestDto {
    private String lastName;
    private String name;
    private String middleName;
    private String email;
    private String login;
    private String password;
    private String role;
}
