package ru.kate.ebook.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import ru.kate.ebook.localStore.BookMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Test {

    private final static ObjectMapper mapper = new ObjectMapper();

    private UUID id = UUID.randomUUID();
    //название теста
    private String name;
    //краткое описание теста
    private String description;
    private List<TestSection> sections = new ArrayList<>();

    @JsonIgnore
    public File getFile() {
        try {
            File file = new File(Paths.get(System.getProperty("java.io.tmpdir")) + File.separator + BookMeta.TEST_NAME);
            mapper.writeValue(file, this);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
