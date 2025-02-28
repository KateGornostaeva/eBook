package ru.kate.ebook.network;

import lombok.Data;

@Data
public class SignInRequestDto {
    private String username;
    private String password;
}
