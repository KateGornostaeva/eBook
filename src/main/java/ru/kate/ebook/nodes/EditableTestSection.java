package ru.kate.ebook.nodes;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.etb.Answer;
import ru.kate.ebook.etb.TestSection;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EditableTestSection extends VBox {

    private final VBox parentVBox;

    private TextField txtQuestion;
    private AnswersBox answersBox;
    private HBox bottomBox;

    public EditableTestSection(VBox parentVBox) {
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
        //
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
            oneIs = false;
            //по хорошему надо сохранить введённые варианты
            getChildren().remove(vBoxAnswers);
            getChildren().remove(bottomBox);
            //а тут их восстановить
            getChildren().add(new AnswersBox(oneIs));
            getChildren().add(bottomBox);
        });
        btnRadio.setOnAction(e -> {
            oneIs = true;
            //по хорошему надо сохранить введённые варианты
            getChildren().remove(vBoxAnswers);
            getChildren().remove(bottomBox);
            //а тут их восстановить
            getChildren().add(new AnswersBox(oneIs));
            getChildren().add(bottomBox);
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

    private void hideShowDelRowButton() {
        if (vBoxAnswers.getChildren().size() > 2) {
            vBoxAnswers.getChildren().stream().filter(obj -> obj instanceof HBox)
                    .map(obj -> (HBox) obj).forEach(hBox1 -> {
                        hBox1.getChildren().stream().filter(obj -> obj instanceof Button)
                                .map(obj -> (Button) obj).forEach(b -> b.setVisible(true));
                    });
        } else {
            vBoxAnswers.getChildren().stream().filter(obj -> obj instanceof HBox)
                    .map(obj -> (HBox) obj).forEach(hBox1 -> {
                        hBox1.getChildren().stream().filter(obj -> obj instanceof Button)
                                .map(obj -> (Button) obj).forEach(b -> b.setVisible(false));
                    });
        }
    }
}
