package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import ru.kate.ebook.network.BookDto;
import ru.kate.ebook.network.Page;
import ru.kate.ebook.nodes.*;
import ru.kate.ebook.test.Test;
import ru.kate.ebook.test.TestSection;
import ru.kate.ebook.utils.CheckTest;
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
    private TextField txtSearch;

    @FXML
    private Button btnUser;

    @FXML
    private Button btnSettings;

    private SplitPane splitPane;
    private ScrollPane sPane;
    private WebView webView;
    private VBox testsBox;
    private ScrollPane runTestPane;
    @Getter
    private boolean grid = true;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
        try {
            drawMainPane();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //навешиваем изображения на кнопки
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setContentDisplay(ContentDisplay.TOP);
        btnSettings.setPrefWidth(100);
        btnSettings.setPrefHeight(60);

        btnBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        btnBack.setContentDisplay(ContentDisplay.TOP);

        txtSearch.setPromptText("Поиск книг на сервере");
        txtSearch.setOnAction(event -> {
            try {
                serverSearch(event);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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
        drawMainPane();
    }

    /**
     * Перерисовка главного окна в зависимости от состояния приложения
     */
    public void drawMainPane() throws URISyntaxException, IOException, InterruptedException {

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

    @FXML
    //действие на кнопку открытия файла (на локальном компе)
    private void handleOpenFile(ActionEvent event) throws IOException, NotSupportedExtension, SQLException, WrongFileFormat {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Электронные книги", "*.pdf", "*.fb2"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        File file = fileChooser.showOpenDialog(ctx.getMainScene().getWindow());
        if (file != null) {
            readMode(file, null);
            //showFile(file);
        }
    }

    @FXML
    private void handleOpenServ(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        ctx.getNetwork().getPageBooks();
    }

    @FXML
    //переключение вида плитки / строчки
    private void handleSwitchList(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {
        grid = !grid;
        drawMainPane();
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

    /**
     * Реакция на поисковый запрос на сервер
     */
    private void serverSearch(ActionEvent event) throws URISyntaxException, IOException, InterruptedException {

        Scene scene = txtSearch.getScene();
        // Получаем координаты сцены
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        // Получаем координаты узла в системе координат сцены
        final Point2D nodeCoord = txtSearch.localToScene(0.0, 0.0);
        // Вычисляем итоговые координаты на экране
        final double screenX = Math.round(scene.getWindow().getX() + sceneCoord.getX() + nodeCoord.getX());
        final double screenY = Math.round(scene.getWindow().getY() + sceneCoord.getY() + nodeCoord.getY());

        List<BookDto> bookDtos = ctx.getNetwork().searchBooks(txtSearch.getText());
        if (bookDtos.isEmpty()) {
            PopupControl popup = new PopupControl();
            popup.setAutoHide(true);
            popup.setAutoFix(true);
            popup.setX(screenX);
            popup.setY(screenY + 60);
            Label label = new Label("По данному запросу на сервере ни чего не найдено");
            label.setPrefWidth(txtSearch.getWidth());
            popup.getScene().setRoot(label);
            popup.show(txtSearch.getScene().getWindow());
        } else {
            List<BookMeta> metas = new ArrayList<>();
            for (BookDto bookDto : bookDtos) {
                BookMeta bookMeta = new BookMeta(bookDto);
                metas.add(bookMeta);
            }
            SearchPopupControl searchPopupControl = new SearchPopupControl(metas, this, txtSearch.getWidth());
            searchPopupControl.setX(screenX);
            searchPopupControl.setY(screenY + 60);
            searchPopupControl.show(txtSearch.getScene().getWindow());
        }
    }

    /**
     * Добавление кнопок книг на панель (режим отображения списка книг)
     */
    private void addBookToPane(Pane pane) throws URISyntaxException, IOException, InterruptedException {

        if (ctx.getRole().equals(Role.ROLE_TEACHER)) {
            addAddTail(pane);
        }

        List<BookMeta> books;
        if (ctx.isConnected()) {
            List<BookMeta> draftBooks = ctx.getLocalStore().getBooks().stream().filter(BookMeta::getIsDraft).toList();
            books = new ArrayList<>();
            books.addAll(draftBooks);
            Page page = ctx.getNetwork().getPageBooks();
            page.getContent().forEach(dto -> {
                BookMeta bookMeta = new BookMeta(dto);
                books.add(bookMeta);
            });
        } else {
            books = ctx.getLocalStore().getBooks();
        }

        List<BookMeta> publishedBooks = new ArrayList<>();
        books.forEach(book -> {
            TileBook tileBook = new TileBook(book, this);
            tileBook.setOnMouseClicked(event -> {
                try {
                    TileBook t = (TileBook) event.getSource();
                    BookMeta meta = t.getMeta();
                    if (event.getButton() == MouseButton.PRIMARY) {
                        File bookFile = ZipBook.getBookFile(meta);
                        readMode(bookFile, meta);
                        //showFile(bookFile);
                    } else {
                        t.showPopup(event.getScreenX(), event.getScreenY());
                    }
                } catch (IOException | NotSupportedExtension | SQLException | WrongFileFormat e) {
                    throw new RuntimeException(e);
                }
            });
            pane.getChildren().add(tileBook);
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
        mainVBox.getChildren().add(buildEditTestToolBar(file, meta));

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

        drawEditTestPane(rightPane, meta);
    }

    /**
     * Отображение режима чтения
     */
    public void readMode(File file, BookMeta meta) throws NotSupportedExtension, SQLException, IOException, WrongFileFormat {
        mainVBox.getChildren().remove(toolBar);
        mainVBox.getChildren().add(buildReadModeToolBar(meta));
        mainVBox.getChildren().remove(splitPane);
        mainVBox.getChildren().remove(sPane);
        mainVBox.getChildren().remove(webView);
        webView = new WebView();
        mainVBox.getChildren().add(webView);
        VBox.setVgrow(webView, Priority.ALWAYS);
        ctx.setWebView(webView);
        ProcessBook processBook = new ProcessBook(ctx);
        processBook.checkExtAndProcess(file);
    }

    /**
     * Рисование главного меню в режиме чтения книги
     */
    private ToolBar buildReadModeToolBar(BookMeta meta) throws IOException {
        ToolBar toolBar = new ToolBar();

        Button btnBack = new Button();
        btnBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        btnBack.setOnAction(event -> {
            mainVBox.getChildren().remove(toolBar);
            mainVBox.getChildren().remove(webView);
            mainVBox.getChildren().remove(runTestPane);
            mainVBox.getChildren().add(this.toolBar);
            try {
                drawMainPane();
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        toolBar.getItems().add(btnBack);

        Button btnRunTest = new Button("Тест");
        Test test;
        if (meta != null) {
            btnRunTest.setDisable(!meta.getIsTestIn());
            Optional<Test> optional = ZipBook.getTest(meta);
            if (optional.isPresent()) {
                test = optional.get();
            } else {
                test = null;
            }
        } else {
            btnRunTest.setDisable(true);
            test = null;
        }
        btnRunTest.setOnAction(event -> {
            mainVBox.getChildren().remove(webView);
            runTestPane = buildRunTestPane(test);
            mainVBox.getChildren().add(runTestPane);

        });
        toolBar.getItems().add(btnRunTest);
        return toolBar;
    }

    private ScrollPane buildRunTestPane(Test test) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox runTestBox = new VBox();
        runTestBox.setSpacing(25);
        runTestBox.setPadding(new Insets(25));
        runTestBox.setAlignment(Pos.CENTER);
        Label label = new Label(test.getName());
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        Button btnResetAndBack = new Button("Сбросить тест\nи вернуться к книге");
        HBox headerBox = new HBox();

        headerBox.setPadding(new Insets(25));
        headerBox.getChildren().addAll(label, pane, btnResetAndBack);
        runTestBox.getChildren().add(headerBox);

        test.getSections().forEach(testSection -> {
            runTestBox.getChildren().add(new RunTestSectionBox(testSection));
        });

        Button btnEndTest = new Button("Завершить тест");
        btnEndTest.setOnAction(event -> {

            List<RunTestSectionBox> runTestSectionBoxes = runTestBox.getChildren().stream()
                    .filter(RunTestSectionBox.class::isInstance).map(RunTestSectionBox.class::cast).toList();

            if (CheckTest.finishCheck(runTestSectionBoxes)) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(ctx.getMainScene().getWindow());
                dialog.setContentText("Завершить тест?");
                dialog.getDialogPane().getButtonTypes().addAll(
                        new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE),
                        new ButtonType("Завершить", ButtonBar.ButtonData.OK_DONE));
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    runTestSectionBoxes.forEach(RunTestSectionBox::finish);
                    runTestBox.getChildren().remove(btnEndTest);
                    Button button = new Button("Результат теста: " + CheckTest.calcResult(test, runTestSectionBoxes));
                    runTestBox.getChildren().add(button);
                }
            } else {
                Dialog<ButtonType> dialog2 = new Dialog<>();
                dialog2.initOwner(ctx.getMainScene().getWindow());
                dialog2.setContentText("Вы ответили не на все вопросы\n\nВсё равно завершить тест?");
                dialog2.getDialogPane().getButtonTypes().addAll(
                        new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE),
                        new ButtonType("Завершить", ButtonBar.ButtonData.OK_DONE));
                Optional<ButtonType> result2 = dialog2.showAndWait();
            }
        });
        runTestBox.getChildren().add(btnEndTest);

        scrollPane.setContent(runTestBox);
        return scrollPane;
    }

    /**
     * Рисование главного меню в режиме редактирования тестов
     */
    private ToolBar buildEditTestToolBar(File file, BookMeta meta) {
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
                drawMainPane();
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
    private void drawEditTestPane(ScrollPane rightPane, BookMeta meta) {
        if (meta != null && meta.getIsTestIn()) {
            try {
                Optional<Test> optional = ZipBook.getTest(meta);
                fillEditTestPane(rightPane, optional.get());
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
                fillEditTestPane(rightPane, null);
            });
        }

    }

    private void fillEditTestPane(ScrollPane rightPane, Test test) {
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
                testsBox.getChildren().add(new EditTestSectionBox(testSection));
            }
        } else {
            testsBox.getChildren().add(new EditTestSectionBox(null));
        }

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        Button newQuestionButton = new Button("Добавить вопрос");
        newQuestionButton.setContentDisplay(ContentDisplay.LEFT);
        newQuestionButton.setGraphic(imageView);
        newQuestionButton.setOnAction(event1 -> {
            testsBox.getChildren().remove(newQuestionButton);
            testsBox.getChildren().add(new EditTestSectionBox(null));
            testsBox.getChildren().add(newQuestionButton);
        });

        testsBox.getChildren().add(newQuestionButton);
    }
}
