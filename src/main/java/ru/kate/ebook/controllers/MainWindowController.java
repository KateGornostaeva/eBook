package ru.kate.ebook.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
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
    public VBox mainVBox;

    @FXML
    public ToolBar mainToolBar;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnDraftAndPublished;

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
    private ToolBar readModeToolBar;
    private ScrollPane runTestPane;
    @Getter
    private boolean grid = true;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
        drawMainPane();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //навешиваем изображения на кнопки
        btnHome.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("home.png"))));
        btnDraftAndPublished.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("draftAndPublished.png"))));

        txtSearch.setPromptText("Поиск книг на сервере");
        txtSearch.setOnAction(event -> {
            try {
                serverSearch(event);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        //btnUser.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("user.png"))));
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
    }

    @FXML
    //действие на кнопку возврата из режима чтения учебника
    public void handleBack(ActionEvent actionEvent) throws URISyntaxException, IOException, InterruptedException {
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
    public void drawMainPane() {
        mainVBox.getChildren().remove(splitPane);
        mainVBox.getChildren().remove(sPane);
        sPane = new ScrollPane();
        sPane.setPrefWidth(mainVBox.getWidth());
        mainVBox.getChildren().add(sPane);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        if (grid) {
            FlowPane flowPane = new FlowPane();
            flowPane.setOrientation(Orientation.VERTICAL);
            flowPane.setVgap(50);
            flowPane.setHgap(50);
            flowPane.setPadding(new Insets(50, 50, 50, 85));
            flowPane.setPrefWidth(sPane.getWidth());
            flowPane.setPrefHeight(sPane.getHeight());
            addBookToPane(flowPane);
            sPane.setContent(flowPane);
            btnListOrGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("list.png"))));
        } else {
            VBox vBox = new VBox();
            vBox.setPrefWidth(sPane.getScene().getWidth() - 10);
            vBox.setPrefHeight(sPane.getHeight());
            vBox.setPadding(new Insets(27, 85, 10, 85));
            vBox.setSpacing(27);
            addBookToPane(vBox);
            sPane.setContent(vBox);
            btnListOrGrid.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("grid.png"))));
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
        }
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
        if (ctx.isConnected()) {
            EbModal profileDialog = new EbModal(null, "profile-dialog", ctx);
            profileDialog.show();
        } else {
            EbModal authDialog = new EbModal(null, "auth-dialog", ctx);
            AuthDialogController controller = (AuthDialogController) authDialog.getController();
            controller.setStage(authDialog);
            authDialog.show();
        }
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
            popup.setMinHeight(64);
            popup.setAutoHide(true);
            popup.setAutoFix(true);
            popup.setX(screenX);
            popup.setY(screenY + 65);

            PauseTransition wait = new PauseTransition(Duration.seconds(3));
            wait.setOnFinished(e -> {
                // Затем начинаем плавное затухание
                FadeTransition fade = new FadeTransition(Duration.seconds(1));
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(fadeEvent -> popup.hide());
                fade.play();
            });

            Label label = new Label("По данному запросу на сервере ничего не найдено");
            label.setPrefWidth(txtSearch.getWidth());
            label.setPrefHeight(txtSearch.getHeight() * 1.7);
            label.setStyle("-fx-background-color: #669999;");
            //label.set
            label.setPadding(new Insets(10, 10, 10, 50));
            popup.getScene().setRoot(label);
            popup.show(txtSearch.getScene().getWindow());
            wait.play();
        } else {
            List<BookMeta> metas = new ArrayList<>();
            for (BookDto bookDto : bookDtos) {
                BookMeta bookMeta = new BookMeta(bookDto);
                metas.add(bookMeta);
            }
            SearchPopupControl searchPopupControl = new SearchPopupControl(metas, this, txtSearch.getWidth());
            searchPopupControl.setAutoFix(true);
            searchPopupControl.setAutoHide(true);
            searchPopupControl.setX(screenX);
            searchPopupControl.setY(screenY + 65);

            PauseTransition wait = new PauseTransition(Duration.seconds(3));
            wait.setOnFinished(e -> {
                // Затем начинаем плавное затухание
                FadeTransition fade = new FadeTransition(Duration.seconds(1));
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(fadeEvent -> searchPopupControl.hide());
                fade.play();
            });

            searchPopupControl.show(txtSearch.getScene().getWindow());
            wait.play();
        }
    }

    /**
     * Добавление кнопок книг на панель (режим отображения списка книг)
     */
    private void addBookToPane(Pane pane) {

        List<BookMeta> books = new ArrayList<>();

        if (ctx.getRole().equals(Role.ROLE_TEACHER)) {
            addAddTail(pane);
            books.addAll(ctx.getLocalStore().getBooks().stream().filter(BookMeta::getIsDraft).toList());
        }
        if (ctx.getRole().equals(Role.ROLE_STUDENT) || ctx.getRole().equals(Role.ROLE_TEACHER)) {
            books.addAll(ctx.getLocalStore().getBooks().stream().filter(BookMeta::getIsNotDraft).toList());
        }
        //если гость, то только с сервера список опубликованных книг
        Page page = ctx.getNetwork().getPageBooks();
        page.getContent().forEach(dto -> {
            BookMeta bookMeta = new BookMeta(dto);
            books.add(bookMeta);
        });

        books.forEach(book -> {
            TileBook tileBook = new TileBook(book, this);
            pane.getChildren().add(tileBook);
        });
    }

    /**
     * Добавление кнопки "Добавление книги"
     *
     * @param pane
     */
    private void addAddTail(Pane pane) {
        AddBookTile addTail = new AddBookTile(this);
        pane.getChildren().add(addTail);
    }

    public void editMode(File file, BookMeta meta) {
        mainVBox.getChildren().remove(mainToolBar);
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
        mainVBox.getChildren().remove(mainToolBar);
        mainVBox.getChildren().add(buildReadModeToolBar(file, meta));
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
    private ToolBar buildReadModeToolBar(File file, BookMeta meta) throws IOException {
        readModeToolBar = new ToolBar();
        readModeToolBar.getStyleClass().add("read-mode-tool-bar");

        Button btnBack = new Button();
        btnBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        btnBack.setPrefHeight(61);
        btnBack.setOnMouseClicked(event -> {
            mainVBox.getChildren().remove(readModeToolBar);
            mainVBox.getChildren().remove(webView);
            mainVBox.getChildren().remove(runTestPane);
            mainVBox.getChildren().add(mainToolBar);
            drawMainPane();
        });
        readModeToolBar.getItems().add(btnBack);

        Button btnContents = new Button();
        btnContents.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("contents.png"))));
        btnContents.setPrefHeight(61);
        btnContents.setOnAction(event -> {

            Point2D localToScreen = readModeToolBar.localToScreen(50, 50);
            Point2D localToScene = readModeToolBar.localToScene(50, 50);
            event.consume();
            Event.fireEvent(readModeToolBar, new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    localToScene.getX(), localToScene.getY(),
                    localToScreen.getX(), localToScreen.getY(),
                    MouseButton.PRIMARY, 1,
                    true, true, true, true, true,
                    true, true, true,
                    true, true, null));
            log.info("Screen: " + localToScreen + " Scene: " + localToScene);
        });
        readModeToolBar.getItems().add(btnContents);

        Button btnReadMode = new Button();
        btnReadMode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("readMode.png"))));
        btnReadMode.setPrefHeight(61);
        readModeToolBar.getItems().add(btnReadMode);

        Button btnZoomIn = new Button();
        btnZoomIn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("zoomIn.png"))));
        btnZoomIn.setPrefHeight(61);
        btnZoomIn.setOnAction(event -> {

        });
        readModeToolBar.getItems().add(btnZoomIn);

        Button btnZoomOut = new Button();
        btnZoomOut.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("zoomOut.png"))));
        btnZoomOut.setPrefHeight(61);
        btnZoomOut.setOnAction(event -> {

        });
        readModeToolBar.getItems().add(btnZoomOut);

        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        readModeToolBar.getItems().add(pane);

        Button btnRunTest = new Button("Тест");
        btnRunTest.setPrefHeight(61);
        btnRunTest.setPrefWidth(150);
        btnRunTest.getStyleClass().add("run-test-button");
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
            mainVBox.getChildren().remove(readModeToolBar);
            ToolBar runTestToolBar = buildRunTestToolBar(file, meta);
            mainVBox.getChildren().add(runTestToolBar);
            runTestPane = buildRunTestPane(test);
            mainVBox.getChildren().add(runTestPane);


        });
        readModeToolBar.getItems().add(btnRunTest);

        Button btnSettings = new Button();
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        btnSettings.setPrefHeight(61);
        readModeToolBar.getItems().add(btnSettings);
        return readModeToolBar;
    }

    private ScrollPane buildRunTestPane(Test test) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox runTestBox = new VBox();
        runTestBox.setSpacing(25);
        runTestBox.setPadding(new Insets(20, 385, 25, 385));
        runTestBox.setAlignment(Pos.CENTER);
        Label label = new Label("Тест");
        label.setStyle("-fx-font-weight: bold;");
        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(25, 0, 0, 0));
        headerBox.getChildren().add(label);
        runTestBox.getChildren().add(headerBox);

        test.getSections().forEach(testSection -> {
            runTestBox.getChildren().add(new RunTestSectionBox(testSection));
        });

        Button btnEndTest = new Button("Завершить тест");
        btnEndTest.getStyleClass().add("btn-end");
        btnEndTest.setOnAction(event -> {

            List<RunTestSectionBox> runTestSectionBoxes = runTestBox.getChildren().stream()
                    .filter(RunTestSectionBox.class::isInstance).map(RunTestSectionBox.class::cast).toList();

            if (CheckTest.finishCheck(runTestSectionBoxes)) {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(ctx.getMainScene().getWindow());
                dialog.getDialogPane().setPrefWidth(500);
                dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(25));
                vBox.setSpacing(35);
                Text text = new Text("Завершить тест?");
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(25);
                Button btnCancel = new Button("Отмена");
                btnCancel.setPrefWidth(200);
                Button btnOk = new Button("Завершить");
                btnOk.setPrefWidth(200);
                btnOk.setStyle("-fx-background-color: #554BA3;");
                btnOk.setOnAction(event1 -> {
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog.close();
                    runTestSectionBoxes.forEach(RunTestSectionBox::finish);
                    runTestBox.getChildren().remove(btnEndTest);
                    Button button = new Button("Результат теста: " + CheckTest.calcResult(test, runTestSectionBoxes));
                    button.setStyle("-fx-background-color: #336666; -fx-text-fill: #FBFBFD");
                    button.setPrefWidth(1200);
                    runTestBox.getChildren().add(button);
                });
                hBox.getChildren().addAll(btnCancel, btnOk);
                vBox.getChildren().addAll(text, hBox);
                dialog.getDialogPane().setContent(vBox);
                dialog.showAndWait();
            } else {
                Dialog<ButtonType> dialog2 = new Dialog<>();
                dialog2.getDialogPane().setPrefWidth(500);
                dialog2.getDialogPane().setStyle("-fx-background-color: #9584E0;");
                dialog2.initOwner(ctx.getMainScene().getWindow());
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(25));
                vBox.setSpacing(35);
                Text text = new Text("Вы ответили не на все вопросы");
                Text text1 = new Text("Всё равно завершить тест?");
                vBox.getChildren().addAll(text, text1);
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(25);
                Button btnCancel = new Button("Отмена");
                btnCancel.setPrefWidth(200);
                btnCancel.setOnAction(event1 -> {
                    dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog2.close();
                });
                Button btnOk = new Button("Завершить");
                btnOk.setPrefWidth(200);
                btnOk.setStyle("-fx-background-color: #554BA3;");
                btnOk.setOnAction(event1 -> {
                    dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    dialog2.close();
                    mainVBox.getChildren().clear();
                    mainVBox.getChildren().add(mainToolBar);
                    drawMainPane();
                });
                hBox.getChildren().addAll(btnCancel, btnOk);
                vBox.getChildren().add(hBox);
                dialog2.getDialogPane().setContent(vBox);
                dialog2.showAndWait();
            }
        });
        runTestBox.getChildren().add(btnEndTest);

        scrollPane.setContent(runTestBox);
        return scrollPane;
    }


    private ToolBar buildRunTestToolBar(File file, BookMeta meta) {
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("run-test-tool-bar");

        Button returnButton = new Button();
        returnButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("back.png"))));
        returnButton.setPrefWidth(100);
        returnButton.setPrefHeight(61);
        returnButton.setOnAction(event -> {
            mainVBox.getChildren().clear();
            try {
                readMode(file, meta);
            } catch (IOException | NotSupportedExtension | SQLException | WrongFileFormat e) {
                throw new RuntimeException(e);
            }

        });

        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        Button resetButton = new Button("Сбросить тест и вернуться к книге");
        resetButton.getStyleClass().add("btn-reset");
        resetButton.setPrefHeight(61);
        resetButton.setOnAction(event -> {
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainToolBar);
            drawMainPane();
        });

        Button button = new Button();
        button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("setting.png"))));
        button.setPrefWidth(100);
        button.setPrefHeight(61);

        toolBar.getItems().add(returnButton);
        toolBar.getItems().addAll(pane);
        toolBar.getItems().add(resetButton);
        toolBar.getItems().add(button);
        return toolBar;
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
            mainVBox.getChildren().add(mainToolBar);
            drawMainPane();
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

        Button delAllTest = new Button("Удалить весь тест");
        delAllTest.setOnAction(event -> {
            drawEditTestPane(rightPane, null);
        });

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("plus.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        Button newQuestionButton = new Button("Добавить вопрос");
        newQuestionButton.setContentDisplay(ContentDisplay.LEFT);
        newQuestionButton.setGraphic(imageView);
        newQuestionButton.setOnAction(event1 -> {
            testsBox.getChildren().remove(newQuestionButton);
            testsBox.getChildren().remove(delAllTest);
            testsBox.getChildren().add(new EditTestSectionBox(null));
            testsBox.getChildren().add(newQuestionButton);
            testsBox.getChildren().add(delAllTest);
        });

        testsBox.getChildren().add(newQuestionButton);


        testsBox.getChildren().add(delAllTest);
        testsBox.setAlignment(Pos.CENTER);
    }

    public void handleHome(ActionEvent actionEvent) {
    }

    public void handleDraftAndPublished(ActionEvent actionEvent) {
    }
}
