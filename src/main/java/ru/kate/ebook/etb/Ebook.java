package ru.kate.ebook.etb;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import lombok.Data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@Data
public class Ebook {

    private final Statement statement;

    public Ebook(File file) throws SQLException {
        Connection connection = DriverManager.getConnection(file.getAbsolutePath());
        statement = connection.createStatement();
    }

    private UUID id;
    private Description description;
    private List<Section> sections;
    private List<Image> images;
    private List<Media> medias;
    private List<Test> tests;
}
