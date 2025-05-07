package ru.kate.ebook.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.controllers.MainWindowController;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.localStore.LocalStore;
import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;

import static javafx.geometry.Pos.TOP_CENTER;

public class TileBook extends AnchorPane {

    @Getter
    private BookMeta meta;
    private final MainWindowController controller;

    public TileBook(BookMeta meta, MainWindowController controller) {
        super();
        this.meta = meta;
        this.controller = controller;
        init(controller.isGrid());
    }

    private void init(boolean grid) {
        getStyleClass().add("tile-book");

        AnchorPane paneInner = new AnchorPane();
        paneInner.getStyleClass().add("tile-book-inner");

        ImageView imageView = new ImageView(meta.getCover());
        imageView.setPreserveRatio(true);

        Label title = new Label(meta.getTitle());
        title.setWrapText(true);


        if (grid) {
            setPrefWidth(250);

            paneInner.setPrefWidth(250);
            paneInner.setPrefHeight(345);

            setLeftAnchor(paneInner, 0.0);
            setRightAnchor(paneInner, 0.0);
            setTopAnchor(paneInner, 0.0);

            imageView.setFitWidth(230);
            setTopAnchor(imageView, 10.0);
            setLeftAnchor(imageView, 10.0);
            setRightAnchor(imageView, 10.0);

            paneInner.getChildren().add(imageView);

            title.setAlignment(TOP_CENTER);
            title.setPrefWidth(230);
            setTopAnchor(title, 355.0);
            setLeftAnchor(title, 10.0);
            setRightAnchor(title, 10.0);

        } else {
            setPrefHeight(120);
            setPrefWidth(500);

            imageView.setFitHeight(90);
            setBottomAnchor(imageView, 0.0);

            title.setAlignment(Pos.CENTER_LEFT);
            title.setPrefWidth(500);
            setLeftAnchor(title, 80.0);
        }

        Pane paneIcon = new Pane();
        paneIcon.setPrefWidth(42);
        paneIcon.setPrefHeight(42);
        ImageView icon = new ImageView();

        if (meta.getPath() == null && !controller.getCtx().getRole().equals(Role.ROLE_GUEST)) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("onServer.png")));

            paneIcon.setOnMouseClicked(e -> {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("(!)");
                dialog.setHeaderText("Вы уверены, что хотите скачать\nучебник?");
                dialog.setContentText("Учебник будет сохранён\nлокально");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.get() == ButtonType.OK) {
                    try {
                        File downloadedZipFile = controller.getCtx().getNetwork().downloadZipFile(meta.getId());
                        Files.copy(downloadedZipFile.toPath(), Path.of(LocalStore.PATH + downloadedZipFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Учебник успешно скопирован");
                        alert.showAndWait();
                        controller.drawMainPane();
                    } catch (URISyntaxException | IOException | InterruptedException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(ex.getMessage());
                        alert.showAndWait();
                    }
                }
                e.consume();
            });

        }

        if (meta.getPath() == null && controller.getCtx().getRole().equals(Role.ROLE_GUEST)) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("noDownlod.png")));
            paneInner.setStyle("-fx-background-color: #33666680");

            paneIcon.setOnMouseClicked(e -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Невозможно скачать учебник");
                alert.setHeaderText("Для скачивания учебника\nнеобходимо авторизоваться на сервере");
                alert.showAndWait();
                e.consume();
            });
        }

        if (meta.getPath() != null) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("offLine.png")));
        }


        setRightAnchor(paneIcon, 2.0);
        setTopAnchor(paneIcon, 2.0);

        paneIcon.getChildren().add(icon);
        getChildren().add(paneInner);
        getChildren().add(title);
        getChildren().add(paneIcon);


        setOnMouseClicked(event -> {
            try {
                TileBook t = (TileBook) event.getSource();
                BookMeta meta = t.getMeta();
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (meta.getPath() == null) {
                        File downloadedZipFile = controller.getCtx().getNetwork().downloadZipFile(meta.getId());
                        BookMeta bookMeta = ZipBook.getBookMeta(downloadedZipFile);
                        File bookFile = ZipBook.getBookFile(bookMeta);
                        controller.readMode(bookFile, bookMeta);
                    } else {
                        File bookFile = ZipBook.getBookFile(meta);
                        controller.readMode(bookFile, meta);
                    }
                } else {
                    t.showPopup(event.getScreenX(), event.getScreenY());
                }
            } catch (IOException | NotSupportedExtension | SQLException | WrongFileFormat | URISyntaxException |
                     InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void showPopup(double x, double y) {
        PopupControl popupControl = new PopupControl();
        popupControl.setAutoHide(true);
        popupControl.setAutoFix(true);
        popupControl.setX(x - 50);
        popupControl.setY(y + 20);

        VBox vBox = new VBox();
        vBox.setStyle("-fx-border-width: 3");

        if (controller.getCtx().getRole().equals(Role.ROLE_TEACHER)) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(16);
            Button btnEdit = new Button("Редактировать");
            btnEdit.setGraphic(imageView);
            btnEdit.setContentDisplay(ContentDisplay.LEFT);
            VBox.setVgrow(btnEdit, Priority.ALWAYS);
            vBox.getChildren().add(btnEdit);

            btnEdit.setOnAction(e -> {
                try {
                    File bookFile = null;
                    if (meta.getPath() == null) {
                        File downloadedZipFile = controller.getCtx().getNetwork().downloadZipFile(meta.getId());
                        BookMeta bookMeta = ZipBook.getBookMeta(downloadedZipFile);
                        bookFile = ZipBook.getBookFile(bookMeta);
                        meta = bookMeta;
                    } else {
                        bookFile = ZipBook.getBookFile(meta);
                    }
                    controller.editMode(bookFile, meta);
                    popupControl.hide();
                } catch (IOException | URISyntaxException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

            });
        }

        if (meta.getIsDraft() != null && meta.getIsDraft()) {
            ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("garbage-can.png")));
            imageView1.setPreserveRatio(true);
            imageView1.setFitHeight(16);
            Button btnDel = new Button("Удалить           ");
            btnDel.setGraphic(imageView1);
            btnDel.setContentDisplay(ContentDisplay.LEFT);
            VBox.setVgrow(btnDel, Priority.ALWAYS);
            vBox.getChildren().add(btnDel);

            btnDel.setOnAction(e -> {
                try {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("(!)");
                    dialog.setHeaderText("Вы уверены, что хотите удалить\nчерновик?");
                    dialog.setContentText("После удаления его нельзя будет\nвосстановить");
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                    Optional<ButtonType> result = dialog.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        Files.deleteIfExists(meta.getPath());
                        ((Pane) getParent()).getChildren().remove(this);
                    }
                    popupControl.hide();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        popupControl.getScene().setRoot(vBox);
        popupControl.show(this.getScene().getWindow());
    }
}
