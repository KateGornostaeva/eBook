package ru.kate.ebook.nodes;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddQuestionButton extends Button {

    public AddQuestionButton() {
        super();
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        setContentDisplay(ContentDisplay.LEFT);
        setGraphic(imageView);
        setText("Добавить вопрос");
    }
}
