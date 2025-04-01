package ru.kate.ebook.nodes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.test.Answer;
import ru.kate.ebook.test.TestSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TestSectionVBox extends VBox {

    private final VBox parentVBox;

    private TextField txtQuestion;
    private AnswersBox answersBox;
    private HBox bottomBox;

    public TestSectionVBox(VBox parentVBox) {
        super();
        this.parentVBox = parentVBox;
        setStyle("-fx-background-color: #888; -fx-padding: 15; -fx-spacing: 15;");

        txtQuestion = new TextField();
        txtQuestion.setPromptText("Введите вопрос");

        Button editButton = new Button();
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        editButton.setGraphic(imageView);
        editButton.setContentDisplay(ContentDisplay.LEFT);
        editButton.setTooltip(new Tooltip("Один из списка\nИли несколько из списка"));
        editButton.setOnMouseClicked(e -> {
            showEditPopup(e.getScreenX(), e.getScreenY());
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

        answersBox = new AnswersBox();
        getChildren().add(answersBox);
        getChildren().add(getBottomBox());

    }

    public TestSection getTestSection() {
        TestSection testSection = new TestSection();
        testSection.setQuestion(txtQuestion.getText());
        List<Answer> answers = new ArrayList<>();
        List<UUID> correctResponses = new ArrayList<>();
        answersBox.getChildren().stream().filter(AnswersBox.class::isInstance).map(AnswersBox.class::cast)
                .forEach(answerBox -> {
                    answerBox.getChildren().stream().filter(AnswerRow.class::isInstance).map(AnswerRow.class::cast)
                            .forEach(row -> {
                                Answer answer = row.getAnswer();
                                answers.add(answer);
                                if (answer.getWeight() > 0) {
                                    correctResponses.add(answer.getUuid());
                                }
                            });
                });
        testSection.setAnswers(answers);
        testSection.setCorrectResponses(correctResponses);
        testSection.setMinValue(correctResponses.size());
        return testSection;
    }

    private void showEditPopup(double x, double y) {
        PopupControl popupControl = new PopupControl();
        popupControl.setAutoHide(true);
        popupControl.setAutoFix(true);
        popupControl.setX(x - 150);
        popupControl.setY(y);


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: #666;" +
                "-fx-padding: 15;" +
                "-fx-border-width: 0");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("radio-button.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        Button btnRadio = new Button("Один из списка         ");
        btnRadio.setGraphic(imageView);
        btnRadio.setContentDisplay(ContentDisplay.LEFT);
        VBox.setVgrow(btnRadio, Priority.ALWAYS);
        vBox.getChildren().add(btnRadio);

        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("checkbox.png")));
        imageView1.setPreserveRatio(true);
        imageView1.setFitHeight(16);
        Button btnCheck = new Button("Несколько из списка");
        btnCheck.setGraphic(imageView1);
        btnCheck.setContentDisplay(ContentDisplay.LEFT);
        VBox.setVgrow(btnCheck, Priority.ALWAYS);
        vBox.getChildren().add(btnCheck);

        popupControl.getScene().setRoot(vBox);
        popupControl.show(this.getScene().getWindow());

        btnCheck.setOnAction(e -> {
            answersBox.setType(false);
        });
        btnRadio.setOnAction(e -> {
            answersBox.setType(true);
        });
    }

    private HBox getBottomBox() {
        Label label = new Label("Отметьте правильный(ые) вариант(ы) ответа(ов)");
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        Button delButton = new Button();
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("garbage-can.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(16);
        delButton.setGraphic(imageView);
        delButton.setOnAction(event -> {
            parentVBox.getChildren().remove(this);
        });
        bottomBox = new HBox();
        bottomBox.getChildren().addAll(label, pane, delButton);
        return bottomBox;
    }
}
