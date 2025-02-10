package ru.kate.ebook.etb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Test {
    private UUID id;
    private String name;
    private String description;
    private List<TestSection> sections = new ArrayList<>();
}
