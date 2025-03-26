package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.Context;
import ru.kate.ebook.ProcessBook;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.nodes.AddBookButton;
import ru.kate.ebook.nodes.EbModal;
import ru.kate.ebook.nodes.EditableTestSection;
import ru.kate.ebook.nodes.Tail;
import ru.kate.ebook.zipBook.ZipBook;

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
    private VBox mainVBox;

    @FXML
    public ToolBar toolBar;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnServ;

    @FXML
    private Button btnListOrGrid;

    @FXML
    private Button btnUser;

    @FXML
    private Button btnSettings;

    private SplitPane splitPane;
    private ScrollPane sPane;
    private WebView webView;
    private boolean grid = true;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
        setUpMainPane();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setContentDisplay(ContentDisplay.TOP);

        btnBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        btnBack.setContentDisplay(ContentDisplay.TOP);

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

    @FXML
    private void handleOpenFile(ActionEvent event) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("Электронный учебник", "*.etb"),
                new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2")
                //new FileChooser.ExtensionFilter("Веб страницы", "*.htm", "*.html")
        );
        File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
        if (file != null) {
            showFile(file);
        }
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        btnBack.setVisible(false);
        btnBack.setPrefWidth(0);
        btnOpen.setVisible(true);
        btnOpen.setPrefWidth(-1.0);
        btnListOrGrid.setDisable(false);
        mainVBox.getChildren().remove(sPane);
        mainVBox.getChildren().remove(webView);
        setUpMainPane();
    }

    @FXML
    private void handleOpenServ(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        ctx.getNetwork().getBooks();
    }

    @FXML
    private void handleSwitchList(ActionEvent event) {
        grid = !grid;
        setUpMainPane();
    }

    @FXML
    private void handleOpenUser(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        EbModal authDialog = new EbModal(null, "auth-dialog", ctx);
        authDialog.show();
    }

    @FXML
    private void handleOpenSettings(ActionEvent event) {

    }

    private void showFile(File file) {
        try {
            btnOpen.setPrefWidth(0);
            btnOpen.setVisible(false);
            btnBack.setVisible(true);
            btnBack.setPrefWidth(-1.0);
            btnListOrGrid.setDisable(true);
            mainVBox.getChildren().remove(splitPane);
            mainVBox.getChildren().remove(sPane);
            mainVBox.getChildren().remove(webView);
            webView = new WebView();
            mainVBox.getChildren().add(webView);
            VBox.setVgrow(webView, Priority.ALWAYS);
            ctx.setWebView(webView);
            ProcessBook processBook = new ProcessBook(ctx);
            processBook.process(file);
        } catch (NotSupportedExtension | SQLException | WrongFileFormat | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUpMainPane() {

        if (ctx.isConnected()) {
            btnServ.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("map.png"))));
            btnServ.setContentDisplay(ContentDisplay.TOP);
            btnServ.setDisable(false);
        } else {
            btnServ.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("no-wifi.png"))));
            btnServ.setContentDisplay(ContentDisplay.TOP);
            btnServ.setDisable(true);
        }
        mainVBox.getChildren().remove(splitPane);
        mainVBox.getChildren().remove(sPane);
        sPane = new ScrollPane();
        sPane.setPrefWidth(mainVBox.getWidth());
        mainVBox.getChildren().add(sPane);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        if (grid) {
            FlowPane flowPane = new FlowPane();
            flowPane.setOrientation(Orientation.VERTICAL);
            flowPane.setVgap(10);
            flowPane.setHgap(10);
            flowPane.setPrefWidth(sPane.getWidth());
            flowPane.setPrefHeight(sPane.getHeight());
            addBookToPane(flowPane);
            sPane.setContent(flowPane);
            btnListOrGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("list.png"))));
            btnListOrGrid.setContentDisplay(ContentDisplay.TOP);
        } else {
            VBox vBox = new VBox();
            vBox.setPrefWidth(sPane.getScene().getWidth());
            vBox.setPrefHeight(sPane.getHeight());
            addBookToPane(vBox);
            sPane.setContent(vBox);
            btnListOrGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("tile.png"))));
            btnListOrGrid.setContentDisplay(ContentDisplay.TOP);

            log.info(sPane.widthProperty().toString() + " " + sPane.heightProperty().toString());
        }
    }

    private void addBookToPane(Pane pane) {

        if (ctx.getRole().equals(Role.ROLE_TEACHER)) {
            addAddTail(pane);
        }

        List<BookMeta> books = List.of();
        if (ctx.isConnected()) {
            //books = ctx.getNetwork().getBooks();
        } else {
            books = ctx.getLocalStore().getBooks();
        }
        books.forEach(book -> {
            ImageView imageView = new ImageView(book.getCover());
            imageView.setPreserveRatio(true);
            if (grid) {
                imageView.setFitHeight(200);
            } else {
                imageView.setFitHeight(32);
            }
            Tail tail = new Tail(book);
            tail.setGraphic(imageView);
            if (grid) {
                tail.setContentDisplay(ContentDisplay.TOP);
            } else {
                tail.setContentDisplay(ContentDisplay.LEFT);
            }
            tail.setText(book.getTitle());
            tail.setOnMouseClicked(event -> {
                try {
                    BookMeta meta = ((Tail) event.getSource()).getMeta();
                    File bookFile = ZipBook.getBookFile(meta);
                    showFile(bookFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pane.getChildren().add(tail);
        });
    }

    private void addAddTail(Pane pane) {
        AddBookButton addTail = new AddBookButton();
        addTail.setText("Добавить учебник");
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        iv.setPreserveRatio(true);
        if (grid) {
            iv.setFitHeight(200);
            addTail.setContentDisplay(ContentDisplay.TOP);
        } else {
            iv.setFitHeight(32);
            addTail.setContentDisplay(ContentDisplay.LEFT);
        }
        addTail.setGraphic(iv);
        pane.getChildren().add(addTail);
        if (!grid) {

            VBox.setVgrow(addTail, Priority.ALWAYS);
        }
        addTail.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Открыть файл");
            fileChooser.getExtensionFilters().addAll(
                    //new FileChooser.ExtensionFilter("Электронный учебник", "*.etb"),
                    new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2")
                    //new FileChooser.ExtensionFilter("Веб страницы", "*.htm", "*.html")
            );
            File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
            if (file != null) {
                mainVBox.getChildren().remove(toolBar);
                mainVBox.getChildren().add(editTestToolBar());

                mainVBox.getChildren().remove(sPane);
                splitPane = new SplitPane();
                splitPane.setOrientation(Orientation.HORIZONTAL);
                splitPane.setDividerPosition(0, 0.5);
                mainVBox.getChildren().add(splitPane);
                VBox.setVgrow(splitPane, Priority.ALWAYS);
                ScrollPane leftPane = new ScrollPane();
                ScrollPane rightPane = new ScrollPane();
                splitPane.getItems().add(leftPane);
                splitPane.getItems().add(rightPane);

                WebView webView = new WebView();
                leftPane.setContent(webView);
                leftPane.setFitToHeight(true);
                leftPane.setFitToWidth(true);
                ctx.setWebView(webView);
                ProcessBook processBook = new ProcessBook(ctx);
                try {
                    processBook.process(file);
                } catch (NotSupportedExtension | WrongFileFormat | IOException | SQLException e) {
                    throw new RuntimeException(e);
                }

                Button addTest = new Button("Создать тест");
                StackPane stackPane = new StackPane();
                stackPane.setAlignment(Pos.CENTER); // Центрируем содержимое
                stackPane.getChildren().add(addTest);
                rightPane.setContent(stackPane);
                rightPane.setFitToWidth(true);
                rightPane.setFitToHeight(true);
                editTest(addTest, rightPane);

            }
        });
    }

    private ToolBar editTestToolBar() {
        ToolBar toolBar = new ToolBar();
        Button localSaveButton = new Button("Сохранить\nлокально");
        localSaveButton.setOnAction(event -> {
            //save in local storage
        });
        toolBar.getItems().add(localSaveButton);

        Button serverSaveButton = new Button("Сохранить и\nопубликовать");
        serverSaveButton.setOnAction(event -> {

        });
        toolBar.getItems().add(serverSaveButton);

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("back.png")));
        iv.setPreserveRatio(true);
        Button backButton = new Button();
        backButton.setGraphic(iv);
        backButton.setOnAction(event -> {
            mainVBox.getChildren().remove(toolBar);
            mainVBox.getChildren().remove(splitPane);
            mainVBox.getChildren().add(this.toolBar);
            mainVBox.getChildren().add(sPane);
        });
        toolBar.getItems().add(backButton);


        return toolBar;
    }

    private void editTest(Button button, ScrollPane rightPane) {
        button.setOnAction(event -> {
            VBox vBox = new VBox();
            vBox.setFillWidth(true);
            vBox.setSpacing(15);
            rightPane.setContent(vBox);
            rightPane.setFitToWidth(true);
            rightPane.setFitToHeight(true);
            Label label = new Label("Создание теста");
            label.setStyle("-fx-font-weight: bold");
            label.setStyle("-fx-font-size: 32px;");
            vBox.getChildren().add(label);
            vBox.getChildren().add(new EditableTestSection(vBox));

            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(32);
            Button newQuestionButton = new Button("Добавить вопрос");
            newQuestionButton.setContentDisplay(ContentDisplay.LEFT);
            newQuestionButton.setGraphic(imageView);
            newQuestionButton.setOnAction(event1 -> {
                vBox.getChildren().remove(newQuestionButton);
                vBox.getChildren().add(new EditableTestSection(vBox));
                vBox.getChildren().add(newQuestionButton);
            });

            vBox.getChildren().add(newQuestionButton);

        });
    }
}
