package ru.kate.ebook.utils;

import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.kate.ebook.Context;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.nodes.TestSectionVBox;
import ru.kate.ebook.test.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Saver {

    /*
     * сохранение учебника в локальное хранилище
     **/
    public static void localSaveAction(File file, Context ctx, VBox testsBox) {

        //формирование объекта с тестами
        Test test = new Test();
        testsBox.getChildren().stream().filter(TestSectionVBox.class::isInstance).map(TestSectionVBox.class::cast)
                .forEach(editTestSection -> {
                    test.getSections().add(editTestSection.getTestSection());
                });

        BookMeta meta = new BookMeta();

        TextInputDialog textInputDialog = new TextInputDialog("Название черновика");
        textInputDialog.setHeaderText("Для сохранения черновика\nвведите название");
        textInputDialog.getEditor().setPrefWidth(300);
        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent()) {
            meta.setTitle(result.get());
        }
        TextInputDialog textInputDialog1 = new TextInputDialog("Краткое описание");
        textInputDialog1.setHeaderText("Введите краткое описание учебника");
        textInputDialog1.getEditor().setPrefWidth(300);
        Optional<String> result1 = textInputDialog1.showAndWait();
        if (result1.isPresent()) {

        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить обложку");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Изображение обложки", "*.png"));
        File fileCover = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());

        try {
            ZipBook.addBookAndTest(file, test.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
