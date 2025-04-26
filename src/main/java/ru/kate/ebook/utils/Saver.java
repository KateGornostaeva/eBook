package ru.kate.ebook.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.Context;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.localStore.LocalStore;
import ru.kate.ebook.network.ProfileDto;
import ru.kate.ebook.nodes.EditTestSectionBox;
import ru.kate.ebook.test.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class Saver {

    /**
     * Сохранение учебника в локальное хранилище
     */
    public static void localSaveAction(File file, BookMeta meta, VBox testsBox) {

        if (meta == null) {
            meta = new BookMeta();
            //запись имени файла учебника для последующего извлечения из архива
            meta.setBookFileName(file.getName());
            meta.setIsDraft(true);
        }

        Test test = getTest(testsBox);
        //запись признака наличия тестов в zip архиве
        if (test.getSections().isEmpty()) {
            meta.setIsTestIn(Boolean.FALSE);
        } else {
            meta.setIsTestIn(Boolean.TRUE);
        }

        TextInputDialog textInputDialog;
        if (meta.getTitle() != null && !meta.getTitle().isEmpty()) {
            textInputDialog = new TextInputDialog(meta.getTitle());
        } else {
            textInputDialog = new TextInputDialog("Название черновика");
        }
        textInputDialog.setHeaderText("Для сохранения черновика\nвведите название");
        textInputDialog.getEditor().setPrefWidth(300);
        Optional<String> result = textInputDialog.showAndWait();
        //запись названия учебника
        result.ifPresent(meta::setTitle);

        try {
            File fileCover = Path.of(Objects.requireNonNull(Saver.class.getResource("draft.png")).toURI()).toFile();
            zipAll(file, test, fileCover, meta);
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage());
        }

    }

    public static void serverSaveAction(File file, Context ctx, VBox testsBox) {
        BookMeta meta = new BookMeta();
        meta.setBookFileName(file.getName());
        meta.setIsDraft(false);

        Test test = getTest(testsBox);
        if (test == null || !test.isCompleted()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Учебник не может быть опубликован на сервер\n" +
                    "\n" +
                    "Пожалуйста, проверьте все ли вопросы доделаны и все ли ответы отмечены ");
            alert.showAndWait();
            return;
        }

        meta.setIsTestIn(Boolean.TRUE);

        try {
            ProfileDto profile = ctx.getNetwork().getProfile();
            meta.setAuthor(profile.getName() + " " + profile.getMiddleName() + " " + profile.getLastName());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        TextInputDialog textInputDialog = new TextInputDialog("Название учебника");
        textInputDialog.setHeaderText("Для сохранения учебника\nвведите название");
        textInputDialog.getEditor().setPrefWidth(300);
        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(meta::setTitle);

        TextInputDialog textInputDialog1 = new TextInputDialog("Краткое описание");
        textInputDialog1.setHeaderText("Введите краткое описание учебника");
        textInputDialog1.getEditor().setPrefWidth(300);
        Optional<String> result1 = textInputDialog1.showAndWait();
        result1.ifPresent(meta::setDescription);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить обложку");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Изображение обложки", "*.png"));
        File fileCover = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
        try {
            Path path = zipAll(file, test, fileCover, meta);
            String code = ctx.getNetwork().upLoadBook("/books/addBook", path.toFile());

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.BASELINE_CENTER);
            vbox.setSpacing(25);
            vbox.setPadding(new Insets(25));
            Label msg = new Label("Книга опубликована");
            Label label = new Label("Код книги: " + code);
            vbox.getChildren().addAll(msg, label);
            PopupControl popup = new PopupControl();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.setPrefWidth(150);
            popup.getScene().setRoot(vbox);
            popup.show(ctx.getMainScene().getWindow());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Формирование объекта с тестами
     *
     * @param testsBox
     * @return
     */
    private static Test getTest(VBox testsBox) {
        Test test = new Test();
        testsBox.getChildren().stream().filter(EditTestSectionBox.class::isInstance).map(EditTestSectionBox.class::cast)
                .forEach(editTestSection -> {
                    test.getSections().add(editTestSection.getTestSection());
                });
        return test;
    }

    private static Path zipAll(File file, Test test, File fileCover, BookMeta meta) throws IOException {
        File bookAndTest = ZipBook.addBookAndTest(file, test.getFile());
        ZipBook.addFile(bookAndTest, fileCover, BookMeta.COVER_NAME);
        Path targetPath;
        if (meta.getPath() != null) {
            targetPath = meta.getPath();
        } else {
            targetPath = Path.of(LocalStore.PATH + UUID.randomUUID() + ".zip");
        }
        Path path = Files.move(bookAndTest.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        ZipBook.addFile(path.toFile(), meta.getFile(), BookMeta.META_NAME);
        return path;
    }

}
