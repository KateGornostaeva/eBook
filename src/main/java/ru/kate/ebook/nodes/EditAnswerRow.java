package ru.kate.ebook.nodes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ru.kate.ebook.test.Answer;

import java.util.UUID;

public class EditAnswerRow extends HBox {

    RadioButton radioButton = new RadioButton();
    CheckBox checkBox = new CheckBox();
    private final ToggleGroup group;
    private boolean oneIs;
    private UUID uuid = UUID.randomUUID();
    private TextField textField;

    public EditAnswerRow(ToggleGroup group, boolean oneIs) {

        super();
        this.group = group;
        this.oneIs = oneIs;

        init();
    }

    public void setType(boolean isOne) {
        this.oneIs = isOne;
        toggleType();
    }

    public void setAnswer(Answer answer) {

        uuid = answer.getUuid();
        textField.setText(answer.getAnswer());
        if (answer.getWeight() > 0) {
            radioButton.setSelected(true);
            checkBox.setSelected(true);
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

        toggleType();

        radioButton.setToggleGroup(group);
        getChildren().add(radioButton);
        getChildren().add(checkBox);

        textField = new TextField();
        textField.setPromptText("Вариант ответа");
        getChildren().add(textField);
        getChildren().add(buildDelRowButton());
    }

    private void toggleType() {
        if (oneIs) {
            radioButton.setVisible(true);
            checkBox.setVisible(false);
        } else {
            checkBox.setVisible(true);
            radioButton.setVisible(false);
        }
    }

    private Button buildDelRowButton() {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("close.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        Button delButton = new Button();
        delButton.setStyle("-fx-background-color: #66999933");
        delButton.setGraphic(imageView);
        delButton.setOnAction(e -> {
            EditAnswersBox parent = (EditAnswersBox) getParent();
            if (parent.getChildren().size() > 2) {
                parent.getChildren().remove(this);
                group.getToggles().remove(radioButton);
            }
        });
        return delButton;
    }
}
