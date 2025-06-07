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
            textField.setText(meta.getTitle());
        } else {
            textField.setText("Название черновика");
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

        AtomicBoolean terminate = new AtomicBoolean(false);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().setStyle("-fx-background-color: #9584E0; -fx-font-size: 24px;\n" +
                "    -fx-background-radius: 10;");
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25));
        vBox.setSpacing(35);
        Text text = new Text("Для сохранения учебника");
        Text text1 = new Text("введите название");
        TextField textField = new TextField();
        textField.setStyle("-fx-background-radius: 10; -fx-prompt-text-fill: #FBFBFD80;");
        if (meta.getTitle() != null && !meta.getTitle().isEmpty()) {
            textField.setText(meta.getTitle());
        } else {
            textField.setText("Название учебника");
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
        meta.setTitle(textField.getText());

        Dialog<ButtonType> dialog2 = new Dialog<>();
        dialog2.initStyle(StageStyle.UNDECORATED);
        dialog2.getDialogPane().setStyle("-fx-background-color: #9584E0; -fx-font-size: 24px;\n" +
                "    -fx-background-radius: 10;");
        VBox vBox2 = new VBox();
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPadding(new Insets(25));
        vBox2.setSpacing(35);
        Text text2 = new Text("Краткое описание");
        Text text3 = new Text("Введите краткое описание учебника");
        TextField textField2 = new TextField();
        textField2.setStyle("-fx-background-radius: 10; -fx-prompt-text-fill: #FBFBFD80;");
        HBox hBox2 = new HBox();
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(25);
        Button btnOk2 = new Button(" Сохранить ");
        btnOk2.setPrefWidth(200);
        btnOk2.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white; -fx-background-radius: 10;");
        btnOk2.setOnAction(event1 -> {
            dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog2.close();
        });
        Button btnCancel2 = new Button("   Отмена   ");
        btnCancel2.setPrefWidth(200);
        btnCancel2.setStyle("-fx-background-radius: 10;");
        btnCancel2.setOnAction(event1 -> {
            dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog2.close();
            terminate.set(true);
        });
        hBox2.getChildren().addAll(btnCancel2, btnOk2);
        vBox2.getChildren().addAll(text2, text3, textField2, hBox2);
        dialog2.getDialogPane().setContent(vBox2);
        dialog2.showAndWait();

        meta.setDescription(textField2.getText());

        if (terminate.get()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить обложку");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Изображение обложки", "*.png"));
        File fileCover = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());

        Dialog<ButtonType> dialog21 = new Dialog<>();
        dialog21.initStyle(StageStyle.UNDECORATED);
        dialog21.getDialogPane().setStyle("-fx-background-color: #9584E0; -fx-font-size: 24px;\n" +
                "    -fx-background-radius: 10;");
        VBox vBox21 = new VBox();
        vBox21.setAlignment(Pos.CENTER);
        vBox21.setPadding(new Insets(25));
        vBox21.setSpacing(25);
        Text text21 = new Text("Вы уверены, что хотите продолжить?");
        Text text31 = new Text("После публикации книг на сервер их нельзя будет");
        text31.setStyle("-fx-font-size: 18;");
        Text text4 = new Text("удалять и изменять название, но можно будет");
        text4.setStyle("-fx-font-size: 18;");
        Text text5 = new Text("редактировать тесты");
        text5.setStyle("-fx-font-size: 18;");
        HBox hBox21 = new HBox();
        hBox21.setAlignment(Pos.CENTER);
        hBox21.setSpacing(25);
        Button btnOk21 = new Button("Опубликовать");
        btnOk21.setPrefWidth(250);
        btnOk21.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white; -fx-background-radius: 10;");
        btnOk21.setOnAction(event1 -> {
            dialog21.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog21.close();
        });
        Button btnCancel21 = new Button("   Отмена   ");
        btnCancel21.setPrefWidth(250);
        btnCancel21.setStyle("-fx-background-radius: 10; -fx-background-color: #FBFBFD;");
        btnCancel21.setOnAction(event1 -> {
            dialog21.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            dialog21.close();
            terminate.set(true);
        });
        hBox21.getChildren().addAll(btnCancel21, btnOk21);
        vBox21.getChildren().addAll(text21, text31, text4, text5, hBox21);
        dialog21.getDialogPane().setContent(vBox21);
        dialog21.showAndWait();

        if (terminate.get()) {
            return;
        }

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
            popup.setPrefWidth(250);
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
