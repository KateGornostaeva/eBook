package ru.kate.ebook.utils;

import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.kate.ebook.Context;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.localStore.LocalStore;
import ru.kate.ebook.nodes.TestSectionVBox;
import ru.kate.ebook.test.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class Saver {

    /*
     * сохранение учебника в локальное хранилище
     **/
    public static void localSaveAction(File file, Context ctx, VBox testsBox) {

        BookMeta meta = new BookMeta();

        //формирование объекта с тестами
        Test test = new Test();
        testsBox.getChildren().stream().filter(TestSectionVBox.class::isInstance).map(TestSectionVBox.class::cast)
                .forEach(editTestSection -> {
                    test.getSections().add(editTestSection.getTestSection());
                });

        if (test.getSections().isEmpty()) {
            meta.setIsTestIn(Boolean.FALSE);
        } else {
            meta.setIsTestIn(Boolean.TRUE);
        }

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
            meta.setDescription(result1.get());
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить обложку");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Изображение обложки", "*.png"));
        File fileCover = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());

        try {
            File bookAndTest = ZipBook.addBookAndTest(file, test.getFile());
            ZipBook.addFile(bookAndTest, fileCover, BookMeta.COVER_NAME);
            Path path = Files.move(bookAndTest.toPath(), Path.of(LocalStore.PATH + bookAndTest.getName()), StandardCopyOption.REPLACE_EXISTING);
            meta.setBookFileName(file.getName());
            ZipBook.addFile(path.toFile(), meta.getFile(), BookMeta.META_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
