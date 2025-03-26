package ru.kate.ebook.nodes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ru.kate.ebook.etb.Answer;

import java.util.UUID;

public class AnswerRow extends HBox {

    RadioButton radioButton = new RadioButton();
    CheckBox checkBox = new CheckBox();
    private final AnswersBox answersBox;
    private final ToggleGroup group;
    private boolean isOne;
    private UUID uuid = UUID.randomUUID();
    private TextField textField;

    public AnswerRow(AnswersBox answersBox, ToggleGroup group, boolean isOne) {

        super();
        this.answersBox = answersBox;
        this.group = group;
        this.isOne = isOne;

        init();
    }

    public void switchType() {
        isOne = !isOne;
        setType();
    }

    public void setAnswer(Answer answer) {

        uuid = answer.getUuid();
        textField.setText(answer.getAnswer());
        if (answer.getWeight() > 0) {
            if (isOne) {
                radioButton.setSelected(true);
            } else {
                checkBox.setSelected(true);
            }
        }
    }

    public Answer getAnswer() {
        Answer answer = new Answer();
        answer.setUuid(uuid);
        answer.setAnswer(textField.getText());
        getChildren().stream().forEach(node -> {
            if (node instanceof RadioButton && ((RadioButton) node).isSelected()) {
                answer.setWeight(1);
            }
            if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                answer.setWeight(1);
            }
        });
        return answer;
    }

    private void init() {

        setSpacing(10);

        setType();

        radioButton.setToggleGroup(group);
        getChildren().add(radioButton);
        getChildren().add(checkBox);

        textField = new TextField();
        textField.setPromptText("Вариант ответа");
        getChildren().add(textField);
        getChildren().add(getDelRowButton());
    }

    private void setType() {
        if (isOne) {
            radioButton.setVisible(true);
            checkBox.setVisible(false);
        } else {
            checkBox.setVisible(true);
            radioButton.setVisible(false);
            getChildren().add(checkBox);
        }
    }

    private Button getDelRowButton() {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("garbage-can.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        Button delButton = new Button();
        delButton.setGraphic(imageView);
        delButton.setOnAction(e -> {
            if (answersBox.getChildren().size() > 2) {
                answersBox.getChildren().remove(this);
                group.getToggles().remove(radioButton);
            }
        });
        return delButton;
    }
}
