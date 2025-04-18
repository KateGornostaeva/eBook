package ru.kate.ebook.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.controllers.MainWindowController;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class TileBook extends AnchorPane {

    @Getter
    private final BookMeta meta;
    private final MainWindowController controller;

    public TileBook(BookMeta meta, MainWindowController controller) {
        super();
        this.meta = meta;
        this.controller = controller;
        init(controller.isGrid());
    }

    private void init(boolean grid) {

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        ImageView imageView = new ImageView(meta.getCover());
        imageView.setPreserveRatio(true);

        Label title = new Label(meta.getTitle());
        title.setWrapText(true);

        if (grid) {
            setPrefHeight(350);
            setPrefWidth(200);

            imageView.setFitHeight(240);
            double ratio = imageView.getImage().getHeight() / 240;
            double width = imageView.getImage().getWidth() / ratio;
            setLeftAnchor(imageView, (200 - width) / 2);

            title.setAlignment(Pos.CENTER);
            title.setPrefWidth(200);
            setTopAnchor(title, 240.0);

        } else {
            setPrefHeight(120);
            setPrefWidth(500);

            imageView.setFitHeight(90);
            setBottomAnchor(imageView, 0.0);

            title.setAlignment(Pos.CENTER_LEFT);
            title.setPrefWidth(500);
            setLeftAnchor(title, 80.0);
        }


        getChildren().add(imageView);
        getChildren().add(title);
//        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("onServer.png")));
//        icon.setPreserveRatio(true);
//        icon.setX(imageView.getFitWidth());
//        Pane pane = new Pane();
//        pane.getChildren().addAll(imageView, icon);
//
//        setGraphic(pane);
//        if (grid) {
//            setContentDisplay(ContentDisplay.TOP);
//        } else {
//            setContentDisplay(ContentDisplay.LEFT);
//        }
//        setText(meta.getTitle());
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
                    File bookFile = ZipBook.getBookFile(meta);
                    controller.editMode(bookFile, meta);
                    popupControl.hide();
                } catch (IOException ex) {
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
