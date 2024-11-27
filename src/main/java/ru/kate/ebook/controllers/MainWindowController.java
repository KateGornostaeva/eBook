package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.Setter;
import org.xml.sax.SAXException;
import ru.kate.ebook.Context;
import ru.kate.ebook.ConverterBook;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {


    @Setter
    protected Context ctx;

    @FXML
    private WebView webView;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnServ;

    @FXML
    private Button btnList;

    @FXML
    private Button btnGrid;

    @FXML
    private Button btnUser;

    @FXML
    private Button btnSettings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnOpen.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("open.png"))));
        btnOpen.setContentDisplay(ContentDisplay.TOP);

        btnServ.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("map.png"))));
        btnServ.setContentDisplay(ContentDisplay.TOP);

        btnList.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("list.png"))));
        btnList.setContentDisplay(ContentDisplay.TOP);

        btnGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("grid.png"))));
        btnGrid.setContentDisplay(ContentDisplay.TOP);

        btnUser.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("user.png"))));
        btnUser.setContentDisplay(ContentDisplay.TOP);

        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setContentDisplay(ContentDisplay.TOP);
    }

    @FXML
    private void handleOpenFile(ActionEvent event) throws ParserConfigurationException, IOException, SAXException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Электронный учебник", "*.etb"),
                new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2"),
                new FileChooser.ExtensionFilter("Веб страницы", "*.htm", "*.html")
        );
        File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
        ConverterBook converterBook = new ConverterBook(ctx);
        String html = converterBook.convertFromFB2(file);
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent(html);
    }

    @FXML
    private void handleOpenServ(ActionEvent event) {

    }

    @FXML
    private void handleOpenList(ActionEvent event) {

    }

    @FXML
    private void handleOpenGrid(ActionEvent event) {

    }

    @FXML
    private void handleOpenUser(ActionEvent event) {

    }

    @FXML
    private void handleOpenSettings(ActionEvent event) {

    }
}
