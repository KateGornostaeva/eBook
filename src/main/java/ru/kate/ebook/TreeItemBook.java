package ru.kate.ebook;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TreeItemBook {
    private String text;
    private String link;
    private String parent;

    @Override
    public String toString() {
        return text;
    }
}
