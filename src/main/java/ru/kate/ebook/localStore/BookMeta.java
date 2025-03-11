package ru.kate.ebook.localStore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.Data;

import java.nio.file.Path;

@Data
public class BookMeta {

    public final static String META_NAME = "bookMeta.json";
    public final static String COVER_NAME = "cover.png";

    private String title;
    private String author;
    private String fileName;

    @JsonIgnore
    private Image cover;

    @JsonIgnore
    private Path path;

    private Boolean isTestIn;
}
