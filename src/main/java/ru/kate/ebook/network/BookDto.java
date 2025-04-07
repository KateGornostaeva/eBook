package ru.kate.ebook.network;

import lombok.Data;

import java.util.UUID;

@Data
public class BookDto {
    private UUID id;
    private String title;
    private String author;
    private Boolean isTestIn;
    private String description;
    private byte[] imageB64;
}
