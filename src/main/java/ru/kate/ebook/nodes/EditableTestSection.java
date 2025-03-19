package ru.kate.ebook.nodes;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EditableTestSection extends VBox {

    private TextField txtQuestion;
    private VBox vBoxAnswers;

    public EditableTestSection() {
        super();
        setStyle("-fx-background-color: #888; -fx-padding: 15");

        txtQuestion = new TextField();
        txtQuestion.setPromptText("Введите вопрос");

        Button editButton = new Button();
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        editButton.setGraphic(imageView);
        editButton.setContentDisplay(ContentDisplay.LEFT);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(txtQuestion);

        Pane pane = new Pane();
        hBox.getChildren().add(pane);
        HBox.setHgrow(pane, Priority.ALWAYS);

        hBox.getChildren().add(editButton);
        getChildren().add(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);


        vBoxAnswers = new VBox();
        getChildren().add(vBoxAnswers);

    }
}
