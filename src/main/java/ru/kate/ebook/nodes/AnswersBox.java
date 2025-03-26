package ru.kate.ebook.nodes;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class AnswersBox extends VBox {

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private boolean oneIs = true;

    public AnswersBox() {
        super();
        init();

    }

    public void toggleType() {
        oneIs = !oneIs;
    }

    private void init() {

        setStyle("-fx-background-color: #aaa; -fx-padding: 15; -fx-spacing: 15;");

        getChildren().addAll(getAnswerRow());

        Button newButton = new Button("Добавить вариант");
        newButton.setOnAction(e -> {
            getChildren().remove(newButton);
            getChildren().add(getAnswerRow());
            getChildren().add(newButton);
        });
        getChildren().add(newButton);
    }

    private AnswerRow getAnswerRow() {
        return new AnswerRow(this, toggleGroup, oneIs);
    }
}
