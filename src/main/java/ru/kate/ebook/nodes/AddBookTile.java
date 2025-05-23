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
        init(controller.isGrid());
    }

    private void init(boolean grid) {

        AnchorPane paneInner = new AnchorPane();
        paneInner.getStyleClass().add("add-book-tile");
        setLeftAnchor(paneInner, 0.0);
        setRightAnchor(paneInner, 0.0);
        setTopAnchor(paneInner, 0.0);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);

        Label title = new Label("Добавить\nучебник");
        title.setWrapText(true);
        title.setAlignment(TOP_CENTER);

        if (grid) {
            setPrefWidth(250);
            setMaxWidth(250);

            paneInner.setPrefWidth(250);
            paneInner.setPrefHeight(345);

            setTopAnchor(imageView, 98.0);
            setLeftAnchor(imageView, 63.0);
            setRightAnchor(imageView, 63.0);

            title.setPrefWidth(230);
            setTopAnchor(title, 355.0);
            setLeftAnchor(title, 10.0);
            setRightAnchor(title, 10.0);
        } else {
            setPrefWidth(150);
            setMaxWidth(150);

            paneInner.setPrefWidth(150);
            paneInner.setMaxWidth(150);
            paneInner.setPrefHeight(205);
            paneInner.setMaxHeight(205);
            //paneInner.setMinHeight(150);

            imageView.setFitWidth(90);

            setTopAnchor(imageView, 57.5);
            setLeftAnchor(imageView, 30.0);
            setRightAnchor(imageView, 30.0);

            title.setPrefWidth(150);
            setTopAnchor(title, 215.0);
            setLeftAnchor(title, 5.0);
            setRightAnchor(title, 5.0);
        }

        paneInner.getChildren().add(imageView);
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
