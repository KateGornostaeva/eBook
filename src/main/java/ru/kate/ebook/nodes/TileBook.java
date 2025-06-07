package ru.kate.ebook.nodes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
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
            setPrefHeight(140);
            setStyle("-fx-background-color: #6699994D;");

            paneInner.setPrefWidth(80);
            paneInner.setPrefHeight(140);
            setLeftAnchor(paneInner, 0.0);
            setTopAnchor(paneInner, 0.0);
            setBottomAnchor(paneInner, 0.0);

            imageView.setFitHeight(90);
            setTopAnchor(imageView, 5.0);
            setLeftAnchor(imageView, 5.0);
            setBottomAnchor(imageView, 5.0);
            setRightAnchor(imageView, 5.0);

            paneInner.getChildren().add(imageView);

            title.setAlignment(Pos.CENTER_LEFT);
            title.setPrefWidth(1500);
            setLeftAnchor(title, 90.0);
            setTopAnchor(title, 30.0);
        }

        Pane paneIcon = new Pane();
        paneIcon.setPrefWidth(42);
        paneIcon.setPrefHeight(42);
        ImageView icon = new ImageView();

        if (meta.getPath() == null && !controller.getCtx().getRole().equals(Role.ROLE_GUEST)) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("onServer.png")));

            paneIcon.setOnMouseClicked(e -> {
                e.consume();
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(controller.getCtx().getMainScene().getWindow());
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(25));
                vBox.setSpacing(35);
                Text text = new Text("Вы уверены, что хотите скачать учебник?");
                Text text1 = new Text("Учебник будет сохранён локально");
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(25);
                Button btnOk = new Button("  OK  ");
                btnOk.setPrefWidth(200);
                btnOk.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white");
                btnOk.setOnAction(event1 -> {
                    try {
                        File downloadedZipFile = controller.getCtx().getNetwork().downloadZipFile(meta.getId());
                        Files.copy(downloadedZipFile.toPath(), Path.of(LocalStore.PATH + downloadedZipFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                        Dialog<ButtonType> dialog2 = new Dialog<>();
                        dialog2.initOwner(controller.getCtx().getMainScene().getWindow());
                        dialog2.initStyle(StageStyle.UNDECORATED);
                        dialog2.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                        VBox vBox2 = new VBox();
                        vBox2.setAlignment(Pos.CENTER);
                        vBox2.setPadding(new Insets(25));
                        vBox2.setSpacing(35);
                        Text text7 = new Text("Учебник успешно скачен");
                        HBox hBox2 = new HBox();
                        hBox2.setAlignment(Pos.CENTER);
                        hBox2.setSpacing(25);
                        Button btnOk2 = new Button("  OK  ");
                        btnOk2.setPrefWidth(200);
                        btnOk2.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white");
                        btnOk2.setOnAction(event2 -> {
                            dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                            dialog2.close();
                        });
                        hBox2.getChildren().addAll(btnOk2);
                        vBox2.getChildren().addAll(text7, hBox2);
                        dialog2.getDialogPane().setContent(vBox2);
                        dialog2.showAndWait();
                        controller.drawMainPane();
                    } catch (URISyntaxException | IOException | InterruptedException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(ex.getMessage());
                        alert.showAndWait();
                    }
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog.close();
                });
                Button btnCancel = new Button("  Cancel  ");
                btnCancel.setPrefWidth(200);
                btnCancel.setOnAction(event1 -> {
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog.close();
                });
                hBox.getChildren().addAll(btnCancel, btnOk);
                vBox.getChildren().addAll(text, text1, hBox);
                dialog.getDialogPane().setContent(vBox);
                dialog.showAndWait();
            });

        }

        if (meta.getPath() == null && controller.getCtx().getRole().equals(Role.ROLE_GUEST)) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("noDownlod.png")));
            paneInner.setStyle("-fx-background-color: #33666680");

            paneIcon.setOnMouseClicked(e -> {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.initOwner(controller.getCtx().getMainScene().getWindow());
                //dialog.getDialogPane().setPrefWidth(200);
                dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(25));
                vBox.setSpacing(35);
                Text text = new Text("Для скачивания учебника");
                Text text1 = new Text("необходимо авторизоваться на сервере");
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(25);
                Button btnOk = new Button("OK");
                btnOk.setPrefWidth(200);
                btnOk.setStyle("-fx-background-color: #554BA3;  -fx-text-fill: #FBFBFD");
                btnOk.setOnAction(event1 -> {
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog.close();
                    event1.consume();
                });
                hBox.getChildren().addAll(btnOk);
                vBox.getChildren().addAll(text, text1, hBox);
                dialog.getDialogPane().setContent(vBox);
                dialog.showAndWait();
                e.consume();
            });
        }

        if (meta.getPath() != null) {
            icon = new ImageView(new Image(getClass().getResourceAsStream("offLine.png")));
        }


        if (grid) {
            setRightAnchor(paneIcon, 2.0);
            setTopAnchor(paneIcon, 2.0);
        } else {
            setRightAnchor(paneIcon, 20.0);
            setTopAnchor(paneIcon, 30.0);
        }


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
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(controller.getCtx().getMainScene().getWindow());
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                VBox vBox2 = new VBox();
                vBox2.setAlignment(Pos.CENTER);
                vBox2.setPadding(new Insets(25));
                vBox2.setSpacing(35);
                Text text = new Text("Вы уверены, что хотите удалить черновик?");
                Text text1 = new Text("После удаления его нельзя будет восстановить");
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(25);
                Button btnOk = new Button("  OK  ");
                btnOk.setPrefWidth(200);
                btnOk.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white");
                btnOk.setOnAction(event1 -> {
                    try {
                        Files.deleteIfExists(meta.getPath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    ((Pane) getParent()).getChildren().remove(this);
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog.close();
                });
                Button btnCancel = new Button("  Отмена  ");
                btnCancel.setPrefWidth(200);
                btnCancel.setOnAction(event1 -> {

                });
                hBox.getChildren().addAll(btnCancel, btnOk);
                vBox2.getChildren().addAll(text, text1, hBox);
                dialog.getDialogPane().setContent(vBox2);
                dialog.showAndWait();

                popupControl.hide();
            });
        }

        popupControl.getScene().setRoot(vBox);
        popupControl.show(this.getScene().getWindow());
    }
}
