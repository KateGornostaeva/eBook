package ru.kate.ebook.etb;

import lombok.Data;

import java.util.UUID;

@Data
public class ColorMark {

    private final String startMark = "<span style=\"background-color: yellow\">";
    private final String endMark = "</span>";

    private UUID uuid;
    private String color;

}
