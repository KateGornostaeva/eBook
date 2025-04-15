package ru.kate.ebook.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import ru.kate.ebook.nodes.RunTestSectionBox;

import java.util.List;

public class CheckTest {

    /**
     * Проверка завершённости теста
     *
     * @param nodes
     * @return true если тест завершён
     */
    public static boolean finishCheck(ObservableList<Node> nodes) {
        List<RunTestSectionBox> testSectionBoxes = nodes.stream().filter(RunTestSectionBox.class::isInstance).map(RunTestSectionBox.class::cast).toList();
        for (RunTestSectionBox testSectionBox : testSectionBoxes) {
            if (!testSectionBox.isChecked()) {
                return false;
            }
        }
        return true;
    }
}
