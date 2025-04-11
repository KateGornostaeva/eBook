package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.Context;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.network.Page;
import ru.kate.ebook.nodes.AddBookButton;
import ru.kate.ebook.nodes.EbModal;
import ru.kate.ebook.nodes.TailBook;
import ru.kate.ebook.nodes.TestSectionVBox;
import ru.kate.ebook.test.Test;
import ru.kate.ebook.test.TestSection;
import ru.kate.ebook.utils.ProcessBook;
import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static ru.kate.ebook.utils.Saver.localSaveAction;
import static ru.kate.ebook.utils.Saver.serverSaveAction;

@Slf4j
public class MainWindowController implements Initializable {

    @Getter
    private Context ctx;

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
    private VBox testsBox;
    @Getter
    private boolean grid = true;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
        try {
            setUpMainPane();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //навешиваем изображения на кнопки
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setContentDisplay(ContentDisplay.TOP);

        btnBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        btnBack.setContentDisplay(ContentDisplay.TOP);
    }

    @FXML
    //действие на кнопку открытия файла (на локальном компе)
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
    //действие на кнопку возврата из режима чтения учебника
    public void handleBack(ActionEvent actionEvent) throws URISyntaxException, IOException, InterruptedException {
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
    //переключение вида плитки / строчки
    private void handleSwitchList(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        grid = !grid;
        setUpMainPane();
    }

    @FXML
    //авторизация пользователя через сервер
    private void handleOpenUser(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        EbModal authDialog = new EbModal(null, "auth-dialog", ctx);
        authDialog.show();
    }

    @FXML
    private void handleOpenSettings(ActionEvent event) {

    }

    //отображение файла (переход в режим чтения)
    public void showFile(File file) {
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
            processBook.checkExtAndProcess(file);
        } catch (NotSupportedExtension | SQLException | WrongFileFormat | IOException e) {
            throw new RuntimeException(e);
        }
    }

    //перерисовка главного окна в зависимости от состояния приложения
    public void setUpMainPane() throws URISyntaxException, IOException, InterruptedException {

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

    //добавление кнопок книг на панель (режим отображения списка книг)
    private void addBookToPane(Pane pane) throws URISyntaxException, IOException, InterruptedException {

        if (ctx.getRole().equals(Role.ROLE_TEACHER)) {
            addAddTail(pane);
        }

        List<BookMeta> books;
        if (ctx.isConnected()) {
            List<BookMeta> draftBooks = ctx.getLocalStore().getBooks().stream().filter(BookMeta::getIsDraft).toList();
            books = new ArrayList<>();
            books.addAll(draftBooks);
            Page page = ctx.getNetwork().getBooks();
            page.getContent().forEach(dto -> {
                BookMeta bookMeta = new BookMeta(dto);
                books.add(bookMeta);
            });
        } else {
            books = ctx.getLocalStore().getBooks();
        }

        List<BookMeta> publishedBooks = new ArrayList<>();
        books.forEach(book -> {
            TailBook tailBook = new TailBook(book, this);
            tailBook.setOnMouseClicked(event -> {
                try {
                    TailBook t = (TailBook) event.getSource();
                    BookMeta meta = t.getMeta();
                    if (event.getButton() == MouseButton.PRIMARY) {
                        File bookFile = ZipBook.getBookFile(meta);
                        showFile(bookFile);
                    } else {
                        t.showPopup(event.getScreenX(), event.getScreenY());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pane.getChildren().add(tailBook);
        });
    }

    /**
     * Добавление кнопки "Добавление книги"
     *
     * @param pane
     */
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
                    new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2")
                    //new FileChooser.ExtensionFilter("Веб страницы", "*.htm", "*.html")
            );
            File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
            if (file != null) {
                editMode(file, null);
            }
        });
    }

    public void editMode(File file, BookMeta meta) {
        mainVBox.getChildren().remove(toolBar);
        mainVBox.getChildren().add(editTestToolBar(file, meta));

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
            processBook.checkExtAndProcess(file);
        } catch (NotSupportedExtension | WrongFileFormat | IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        editTestPane(rightPane, file, meta);
    }

    /**
     * Рисование главного меню в режиме редактирования тестов
     */
    private ToolBar editTestToolBar(File file, BookMeta meta) {
        ToolBar toolBar = new ToolBar();
        Button localSaveButton = new Button("Сохранить\nлокально");
        localSaveButton.setOnAction(event -> {
            localSaveAction(file, meta, testsBox);
        });
        toolBar.getItems().add(localSaveButton);

        Button serverSaveButton = new Button("Сохранить и\nопубликовать");
        serverSaveButton.setOnAction(event -> {
            serverSaveAction(file, ctx, testsBox);
        });
        if (ctx.isConnected()) {
            serverSaveButton.setDisable(false);
        } else {
            serverSaveButton.setDisable(true);
        }
        toolBar.getItems().add(serverSaveButton);

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("back.png")));
        iv.setPreserveRatio(true);
        Button backButton = new Button();
        backButton.setGraphic(iv);
        backButton.setOnAction(event -> {
            mainVBox.getChildren().remove(toolBar);
            mainVBox.getChildren().remove(splitPane);
            mainVBox.getChildren().add(this.toolBar);
            try {
                setUpMainPane();
            } catch (URISyntaxException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        toolBar.getItems().add(backButton);


        return toolBar;
    }

    /**
     * Создание панели редактирования тестов
     */
    private void editTestPane(ScrollPane rightPane, File file, BookMeta meta) {
        if (meta != null && meta.getIsTestIn()) {
            try {
                Optional<Test> optional = ZipBook.getTest(meta);
                drawEditTestPane(rightPane, optional.get());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //заполняем редактор тестов данными
        } else {
            Button addTest = new Button("Создать тест");
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER); // Центрируем содержимое
            stackPane.getChildren().add(addTest);
            rightPane.setContent(stackPane);
            rightPane.setFitToWidth(true);
            rightPane.setFitToHeight(true);
            addTest.setOnAction(event -> {
                drawEditTestPane(rightPane, null);
            });
        }

    }

    private void drawEditTestPane(ScrollPane rightPane, Test test) {
        testsBox = new VBox();
        testsBox.setFillWidth(true);
        testsBox.setSpacing(15);

        rightPane.setContent(testsBox);
        rightPane.setFitToWidth(true);
        rightPane.setFitToHeight(true);

        Label label = new Label("Редактирование теста");
        label.setStyle("-fx-font-weight: bold");
        label.setStyle("-fx-font-size: 32px;");
        testsBox.getChildren().add(label);

        if (test != null) {
            for (TestSection testSection : test.getSections()) {
                testsBox.getChildren().add(new TestSectionVBox(testSection));
            }
        } else {
            testsBox.getChildren().add(new TestSectionVBox(null));
        }

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        Button newQuestionButton = new Button("Добавить вопрос");
        newQuestionButton.setContentDisplay(ContentDisplay.LEFT);
        newQuestionButton.setGraphic(imageView);
        newQuestionButton.setOnAction(event1 -> {
            testsBox.getChildren().remove(newQuestionButton);
            testsBox.getChildren().add(new TestSectionVBox(null));
            testsBox.getChildren().add(newQuestionButton);
        });

        testsBox.getChildren().add(newQuestionButton);
    }
}
