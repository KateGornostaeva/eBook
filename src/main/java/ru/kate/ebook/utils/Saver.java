package ru.kate.ebook.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
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
import java.util.concurrent.atomic.AtomicBoolean;

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

        AtomicBoolean terminate = new AtomicBoolean(false);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);
        //dialog.initOwner(ctx.getMainScene().getWindow());
        dialog.getDialogPane().setStyle("-fx-background-color: #9584E0; -fx-font-size: 24px;\n" +
                "    -fx-background-radius: 10;");
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25));
        vBox.setSpacing(35);
        Text text = new Text("Для сохранения черновика");
        Text text1 = new Text("введите название");
        TextField textField = new TextField();
        textField.setStyle("-fx-background-radius: 10; -fx-prompt-text-fill: #FBFBFD80;");
        if (meta.getTitle() != null && !meta.getTitle().isEmpty()) {
            textField.setPromptText(meta.getTitle());
        } else {
            textField.setPromptText("Название черновика");
        }
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button btnOk = new Button(" Сохранить ");
        btnOk.setPrefWidth(200);
        btnOk.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white; -fx-background-radius: 10;");
        btnOk.setOnAction(event1 -> {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog.close();
        });
        Button btnCancel = new Button("   Отмена   ");
        btnCancel.setPrefWidth(200);
        btnCancel.setStyle("-fx-background-radius: 10;");
        btnCancel.setOnAction(event1 -> {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog.close();
            terminate.set(true);
        });
        hBox.getChildren().addAll(btnCancel, btnOk);
        vBox.getChildren().addAll(text, text1, textField, hBox);
        dialog.getDialogPane().setContent(vBox);
        dialog.showAndWait();

        if (terminate.get()) {
            return;
        }

        //запись названия учебника
        meta.setTitle(textField.getText());

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
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initOwner(ctx.getMainScene().getWindow());
            //dialog.getDialogPane().setPrefWidth(500);
            dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(25));
            vBox.setSpacing(35);
            Text text = new Text("Учебник не может быть опубликован на сервер");
            Text text1 = new Text("Пожалуйста, проверьте все ли вопросы доделаны и все ли ответы отмечены");
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(25);
            Button btnOk = new Button("  OK  ");
            btnOk.setPrefWidth(200);
            btnOk.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white");
            btnOk.setOnAction(event1 -> {
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                dialog.close();
            });
            hBox.getChildren().addAll(btnOk);
            vBox.getChildren().addAll(text, text1, hBox);
            dialog.getDialogPane().setContent(vBox);
            dialog.showAndWait();
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
            vbox.setStyle("-fx-background-color: #9584E0");
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
