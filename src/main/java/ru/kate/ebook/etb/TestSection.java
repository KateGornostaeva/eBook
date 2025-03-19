package ru.kate.ebook.etb;

import lombok.Data;

import java.util.List;

@Data
public class TestSection {
    private int id;
    private String question;
    private int minValue;
    private List<Integer> correctResponses;
    private List<Answer> answers;
}
