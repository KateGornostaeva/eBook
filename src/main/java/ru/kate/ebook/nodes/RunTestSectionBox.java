package ru.kate.ebook.nodes;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import ru.kate.ebook.test.TestSection;

import java.util.List;

public class RunTestSectionBox extends VBox {

    private final TestSection testSection;
    private final ToggleGroup toggleGroup = new ToggleGroup();

    public RunTestSectionBox(TestSection testSection) {
        super();
        this.testSection = testSection;
        getStyleClass().add("run-test-section");
        init();
    }

    public void finish() {
        List<RunAnswerRow> runAnswerRows = getChildren().stream().filter(RunAnswerRow.class::isInstance).map(RunAnswerRow.class::cast).toList();
        for (RunAnswerRow runAnswerRow : runAnswerRows) {
            if (testSection.getCorrectResponses().contains(runAnswerRow.getAnswerId())
                    && runAnswerRow.isSelected()) {
                runAnswerRow.finish(true);
            }
            if (!testSection.getCorrectResponses().contains(runAnswerRow.getAnswerId())
                    && runAnswerRow.isSelected()) {
                runAnswerRow.finish(false);
            }
            if (!testSection.getOneIs()) {
                if (testSection.getCorrectResponses().contains(runAnswerRow.getAnswerId())) {
                    runAnswerRow.finish(true);
                }
            }
        }
    }

    public boolean isChecked() {
        List<RunAnswerRow> runAnswerRows = getChildren().stream().filter(RunAnswerRow.class::isInstance).map(RunAnswerRow.class::cast).toList();
        for (RunAnswerRow runAnswerRow : runAnswerRows) {
            if (runAnswerRow.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public int getResult() {
        int result = 0;
        List<RunAnswerRow> runAnswerRows = getChildren().stream().filter(RunAnswerRow.class::isInstance).map(RunAnswerRow.class::cast).toList();
        for (RunAnswerRow runAnswerRow : runAnswerRows) {
            if (runAnswerRow.isSelected() && testSection.getCorrectResponses().contains(runAnswerRow.getAnswerId())) {
                result++;
            }
        }
        if (result >= testSection.getMinValue()) {
            return 1;
        } else {
            return 0;
        }
    }

    private void init() {
        setSpacing(10);
        setPadding(new Insets(25));
        Label question = new Label(testSection.getQuestion());
        getChildren().add(question);
        testSection.getAnswers().forEach(answer -> {
            RunAnswerRow answerRow = new RunAnswerRow(answer, testSection.getOneIs(), toggleGroup);
            getChildren().add(answerRow);
        });
    }
}
