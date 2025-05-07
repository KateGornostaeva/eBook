package ru.kate.ebook.nodes;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import ru.kate.ebook.controllers.MainWindowController;

import java.io.File;

import static javafx.geometry.Pos.TOP_CENTER;

public class AddBookTile extends AnchorPane {

    private final MainWindowController controller;

    public AddBookTile(MainWindowController controller) {
        super();
        this.controller = controller;
        init();
    }

    private void init() {

        AnchorPane paneInner = new AnchorPane();
        paneInner.getStyleClass().add("add-book-tile");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);

        Label title = new Label("Добавить\nучебник");
        title.setWrapText(true);

        setPrefWidth(250);

        paneInner.setPrefWidth(250);
        paneInner.setPrefHeight(345);

        setLeftAnchor(paneInner, 0.0);
        setRightAnchor(paneInner, 0.0);
        setTopAnchor(paneInner, 0.0);

        setTopAnchor(imageView, 98.0);
        setLeftAnchor(imageView, 63.0);
        setRightAnchor(imageView, 63.0);

        paneInner.getChildren().add(imageView);

        title.setAlignment(TOP_CENTER);
        title.setPrefWidth(230);
        setTopAnchor(title, 355.0);
        setLeftAnchor(title, 10.0);
        setRightAnchor(title, 10.0);

        getChildren().add(paneInner);
        getChildren().add(title);

        setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Открыть файл");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2")
            );
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                controller.editMode(file, null);
            }
        });
    }
}
