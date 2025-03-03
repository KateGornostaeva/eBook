package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.Context;
import ru.kate.ebook.ProcessBook;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.localStore.BookMeta;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class MainWindowController implements Initializable {

    protected Context ctx;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnServ;

    @FXML
    private Button btnListOrGrid;

    @FXML
    private Button btnUser;

    @FXML
    private Button btnSettings;

    @FXML
    private ScrollPane sPane;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
        setUpMainPane();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //btnOpen.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("open.png"))));
        //btnOpen.setContentDisplay(ContentDisplay.TOP);

        btnServ.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("map.png"))));
        btnServ.setContentDisplay(ContentDisplay.TOP);

        btnListOrGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("list.png"))));
        btnListOrGrid.setContentDisplay(ContentDisplay.TOP);

        btnUser.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("user.png"))));
        btnUser.setContentDisplay(ContentDisplay.TOP);

        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setContentDisplay(ContentDisplay.TOP);

//        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            TreeItem<TreeItemBook> selectedItem = treeView.getFocusModel().getFocusedItem();
//            String link = selectedItem.getValue().getLink();
//            if (link != null && !link.isEmpty()) {
//                try {
//                    JSObject window = (JSObject) webView.getEngine().executeScript("window");
//                    window.call("jump", link);
//                } catch (JSException e) {
//                    log.error(e.getMessage());
//                }
//            }
//        });
    }

    private void setUpMainPane() {
        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        List<BookMeta> books = ctx.getLocalStore().getBooks();
        books.forEach(book -> {
            ImageView imageView = new ImageView(book.getCover());
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(200);
            Button btn = new Button();
            btn.setGraphic(imageView);
            btn.setContentDisplay(ContentDisplay.TOP);
            btn.setText(book.getTitle());
            flowPane.getChildren().add(btn);
        });

        sPane.setContent(flowPane);
    }

    @FXML
    private void handleOpenFile(ActionEvent event) throws IOException {
        // проверить, были ли изменения в текущей книге и если что, сохранить
        //сбросить состояния связанные с книгой
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Электронный учебник", "*.etb"),
                new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2"),
                new FileChooser.ExtensionFilter("Веб страницы", "*.htm", "*.html")
        );
        File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());

        //ctx.setWebView(webView);
        //ctx.setTreeView(treeView);
        ProcessBook processBook = new ProcessBook(ctx);
        try {
            processBook.process(file);
        } catch (NotSupportedExtension e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (WrongFileFormat e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleOpenServ(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        ctx.getNetwork().getBooks();
    }

    @FXML
    private void handleSwitchList(ActionEvent event) {

    }

    @FXML
    private void handleOpenUser(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {

        ctx.getNetwork().signUp();
    }

    @FXML
    private void handleOpenSettings(ActionEvent event) {

    }
}
