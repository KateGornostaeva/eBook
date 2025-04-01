package ru.kate.ebook.localStore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.Data;

import java.nio.file.Path;

@Data
public class BookMeta {

    public final static String META_NAME = "bookMeta.json";
    public final static String COVER_NAME = "cover.png";
    public final static String TEST_NAME = "test.json";

    //название учебника
    private String title;

    //автор учебника (берётся из кредов пользователя)
    private String author;

    //имя файла учебника в архиве
    private String bookFileName;

    @JsonIgnore
    private Image cover;

    @JsonIgnore
    //указывает полный путь на файл с zip архивом с учебником и прочим
    private Path path;

    //признак наличия теста
    private Boolean isTestIn;

    //Краткое описание учебника
    private String description;
}
