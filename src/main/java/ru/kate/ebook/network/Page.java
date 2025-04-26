package ru.kate.ebook.network;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Page {
    private List<BookDto> content = new ArrayList<>();
    private Map<String, Object> pageable;
    private Boolean last;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private Integer number;
    private Map<String, Object> sort;
    private Boolean first;
    private Integer numberOfElements;
    private Boolean empty;
}
