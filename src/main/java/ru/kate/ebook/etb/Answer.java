package ru.kate.ebook.etb;

import lombok.Data;

@Data
public class Answer {
    private int id;
    private int testSectionId;
    private String answer;
    private int weight;

}
