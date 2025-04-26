package ru.kate.ebook.network;

import lombok.Data;

@Data
public class ProfileDto {
    private String username;
    private String email;
    private String role;
    private String name;
    private String lastName;
    private String middleName;
}
