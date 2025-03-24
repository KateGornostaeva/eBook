package ru.kate.ebook.nodes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EditableTestSection extends VBox {

    private TextField txtQuestion;
    private VBox vBoxAnswers;
    private boolean oneIs = true;
    ToggleGroup group;

    public EditableTestSection() {
        super();
        setStyle("-fx-background-color: #888; -fx-padding: 15; -fx-spacing: 15;");

        txtQuestion = new TextField();
        txtQuestion.setPromptText("Введите вопрос");

        Button editButton = new Button();
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        editButton.setGraphic(imageView);
        editButton.setContentDisplay(ContentDisplay.LEFT);
        editButton.setOnAction(e -> {
            oneIs = !oneIs;
            //по хорошему надо сохранить введённые варианты
            getChildren().remove(vBoxAnswers);
            //а тут их восстановить
            getChildren().add(getVBoxAnswers());
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(txtQuestion);

        Pane pane = new Pane();
        hBox.getChildren().add(pane);
        HBox.setHgrow(pane, Priority.ALWAYS);

        hBox.getChildren().add(editButton);
        getChildren().add(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        getChildren().add(getVBoxAnswers());
    }

    private VBox getVBoxAnswers() {
        vBoxAnswers = new VBox();
        vBoxAnswers.setStyle("-fx-background-color: #aaa; -fx-padding: 15; -fx-spacing: 15;");
        if (oneIs) {
            group = new ToggleGroup();
            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(group);
            TextField textField = new TextField();
            textField.setPromptText("Вариант ответа");
            Button newButton = new Button("Добавить вариант");
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().add(radioButton);
            hBox.getChildren().add(textField);
            vBoxAnswers.getChildren().add(hBox);
            vBoxAnswers.getChildren().add(newButton);

            newButton.setOnAction(e -> {
                RadioButton radioButton1 = new RadioButton();
                radioButton1.setToggleGroup(group);
                TextField textField1 = new TextField();
                textField1.setPromptText("Вариант ответа");
                HBox hBox1 = new HBox();
                hBox1.setSpacing(10);
                hBox1.getChildren().add(radioButton1);
                hBox1.getChildren().add(textField1);
                vBoxAnswers.getChildren().remove(newButton);
                vBoxAnswers.getChildren().add(hBox1);
                vBoxAnswers.getChildren().add(newButton);
            });
        } else {
            CheckBox checkBox = new CheckBox();
            TextField textField = new TextField();
            textField.setPromptText("Вариант ответа");
            Button newButton = new Button("Добавить вариант");
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().add(checkBox);
            hBox.getChildren().add(textField);
            vBoxAnswers.getChildren().add(hBox);
            vBoxAnswers.getChildren().add(newButton);
            newButton.setOnAction(e -> {
                CheckBox checkBox1 = new CheckBox();
                TextField textField1 = new TextField();
                textField1.setPromptText("Вариант ответа");
                HBox hBox1 = new HBox();
                hBox1.setSpacing(10);
                hBox1.getChildren().add(checkBox1);
                hBox1.getChildren().add(textField1);
                vBoxAnswers.getChildren().remove(newButton);
                vBoxAnswers.getChildren().add(hBox1);
                vBoxAnswers.getChildren().add(newButton);
            });
        }
        return vBoxAnswers;
    }
}
