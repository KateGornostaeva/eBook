package ru.kate.ebook.entity;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Ebook {

    private UUID id;
    private Description description;
    private List<Section> sections;
    private List<Image> images;
    private List<Media> medias;
    private List<Test> tests;
}
