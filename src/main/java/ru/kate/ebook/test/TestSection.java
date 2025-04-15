package ru.kate.ebook.test;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TestSection {
    private UUID id = UUID.randomUUID();
    private String question;
    private Integer minValue = 0;
    private Boolean oneIs;
    private List<UUID> correctResponses;
    private List<Answer> answers;
}
