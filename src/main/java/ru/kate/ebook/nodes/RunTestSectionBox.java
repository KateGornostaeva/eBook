package ru.kate.ebook.nodes;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import ru.kate.ebook.test.TestSection;

public class RunTestSectionBox extends VBox {

    private final TestSection testSection;
    private final ToggleGroup toggleGroup = new ToggleGroup();

    public RunTestSectionBox(TestSection testSection) {
        super();
        this.testSection = testSection;
        init();
    }

    private void init() {
        setSpacing(10);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: gray;");
        Label question = new Label(testSection.getQuestion());
        getChildren().add(question);
        testSection.getAnswers().forEach(answer -> {
            RunAnswerRow answerRow = new RunAnswerRow(answer, testSection.getOneIs(), toggleGroup);
            getChildren().add(answerRow);
        });
    }
}
