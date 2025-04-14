package ru.kate.ebook.nodes;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import ru.kate.ebook.test.Answer;

import java.util.List;

public class EditAnswersBox extends VBox {

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private boolean oneIs = true;

    public EditAnswersBox(List<Answer> answers) {
        super();
        init(answers);

    }

    public void setType(boolean oneIs) {
        this.oneIs = oneIs;
        getChildren().stream().filter(EditAnswerRow.class::isInstance).map(EditAnswerRow.class::cast)
                .forEach(row -> {
                    row.setType(this.oneIs);
                });
    }

    public Boolean getType() {
        return this.oneIs;
    }

    private void init(List<Answer> answers) {

        setStyle("-fx-background-color: #aaa; -fx-padding: 15; -fx-spacing: 15;");

        if (answers != null && !answers.isEmpty()) {
            for (Answer answer : answers) {
                EditAnswerRow editAnswerRow = new EditAnswerRow(toggleGroup, oneIs);
                editAnswerRow.setAnswer(answer);
                getChildren().add(editAnswerRow);
            }
        } else {
            getChildren().add(new EditAnswerRow(toggleGroup, oneIs));
        }
        Button newButton = new Button("Добавить вариант");
        newButton.setOnAction(e -> {
            getChildren().remove(newButton);
            getChildren().add(new EditAnswerRow(toggleGroup, oneIs));
            getChildren().add(newButton);
        });
        getChildren().add(newButton);
    }
}
