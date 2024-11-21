package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.Setter;
import ru.kate.ebook.Context;

import java.io.File;

public class MainWindowController {

    @Setter
    protected Context ctx;

    @FXML
    private WebView webView;

    @FXML
    private void handleOpenFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
        WebEngine webEngine = webView.getEngine();
        webEngine.load(file.toURI().toString());
    }
}
