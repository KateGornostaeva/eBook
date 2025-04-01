package ru.kate.ebook.test;

import lombok.Data;

import java.util.UUID;

@Data
public class Answer {
    private UUID uuid;
    private String answer;
    private int weight;

}
