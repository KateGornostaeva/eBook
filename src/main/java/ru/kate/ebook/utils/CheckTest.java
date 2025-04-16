package ru.kate.ebook.utils;

import ru.kate.ebook.nodes.RunTestSectionBox;
import ru.kate.ebook.test.Test;

import java.util.List;

public class CheckTest {

    /**
     * Проверка завершённости теста
     *
     * @param runTestSectionBoxes
     * @return true если тест завершён
     */
    public static boolean finishCheck(List<RunTestSectionBox> runTestSectionBoxes) {
        for (RunTestSectionBox testSectionBox : runTestSectionBoxes) {
            if (!testSectionBox.isChecked()) {
                return false;
            }
        }
        return true;
    }

    public static String calcResult(Test test, List<RunTestSectionBox> runTestSectionBoxes) {
        int rightAnswer = 0;
        for (RunTestSectionBox testSectionBox : runTestSectionBoxes) {
            rightAnswer += testSectionBox.getResult();
        }
        return String.valueOf(rightAnswer) + "/" + test.getSections().size();
    }
}
