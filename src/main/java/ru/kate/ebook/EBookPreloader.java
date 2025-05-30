package ru.kate.ebook;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EBookPreloader extends Preloader {

    private Stage stage;

    public static void main(final String[] args) {
        System.setProperty("javafx.preloader", "ru.kate.ebook.EBookPreloader");
        Application.launch(EBookMain.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Image img;
        try {
            img = new Image(getClass().getResourceAsStream("img/booki.jpg"));
            if (img.isError()) throw new RuntimeException("Error loading preload image");
        } catch (Exception e) {
            log.error("Error loading preload image", e);
            return;
        }

        this.stage = stage;
        Scene scene = new Scene(new VBox(new ImageView(img)));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("Load game");
        stage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof StateChangeNotification) {
            this.stage.close();
        }
    }
}
