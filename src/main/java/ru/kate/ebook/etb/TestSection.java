package ru.kate.ebook.etb;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TestSection {
    private int id;
    private UUID testId;
    private String question;
    private int minValue;
    private List<Integer> correctResponses;
    private List<Answer> answers;
}
