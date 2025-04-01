package ru.kate.ebook.test;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TestSection {
    private UUID id = UUID.randomUUID();
    private String question;
    private int minValue;
    private List<UUID> correctResponses;
    private List<Answer> answers;
}
