package ru.kate.ebook.nodes;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import ru.kate.ebook.test.Answer;

public class RunAnswerRow extends HBox {

    private final Answer answer;
    private final boolean oneIs;

    public RunAnswerRow(Answer answer, boolean oneIs, ToggleGroup toggleGroup) {
        super();
        this.answer = answer;
        this.oneIs = oneIs;
        init(toggleGroup);
    }

    private void init(ToggleGroup toggleGroup) {
        setPadding(new Insets(5, 5, 5, 30));
        if (oneIs) {
            RadioButton oneRadio = new RadioButton(answer.getAnswer());
            oneRadio.setToggleGroup(toggleGroup);
            getChildren().add(oneRadio);
        } else {
            CheckBox oneCheckBox = new CheckBox(answer.getAnswer());
            getChildren().add(oneCheckBox);
        }
    }
}
