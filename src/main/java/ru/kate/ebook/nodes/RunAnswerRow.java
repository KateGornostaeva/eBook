package ru.kate.ebook.nodes;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import ru.kate.ebook.test.Answer;

import java.util.UUID;

public class RunAnswerRow extends HBox {

    private final Answer answer;
    private final boolean oneIs;
    private RadioButton oneRadio = null;
    private CheckBox oneCheckBox = null;

    public RunAnswerRow(Answer answer, boolean oneIs, ToggleGroup toggleGroup) {
        super();
        this.answer = answer;
        this.oneIs = oneIs;
        init(toggleGroup);
    }


    /**
     * Показать результат прохождения теста
     *
     * @param success true - вопрос красим в зелёный
     *                false - в красный
     */
    public void finish(boolean success) {
        String color;
        if (success) {
            color = "green";
        } else {
            color = "red";
        }

        if (oneIs) {
            oneRadio.setStyle("-fx-text-fill: " + color + ";");
        } else {
            oneCheckBox.setStyle("-fx-text-fill: " + color + ";");
        }

    }

    public boolean isSelected() {
        if (oneIs) {
            return oneRadio.isSelected();
        } else {
            return oneCheckBox.isSelected();
        }
    }

    public UUID getAnswerId() {
        return answer.getUuid();
    }

    private void init(ToggleGroup toggleGroup) {
        setPadding(new Insets(5, 5, 5, 30));
        if (oneIs) {
            oneRadio = new RadioButton(answer.getAnswer());
            oneRadio.setToggleGroup(toggleGroup);
            getChildren().add(oneRadio);
        } else {
            oneCheckBox = new CheckBox(answer.getAnswer());
            getChildren().add(oneCheckBox);
        }
    }
}
