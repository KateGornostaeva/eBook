package ru.kate.ebook;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kate.ebook.controllers.MainWindowController;

import java.io.File;
import java.util.ResourceBundle;

public class EBookMain extends Application {

    private Context ctx;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        ctx = new Context(this);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Thread.sleep(1000);
        this.notifyPreloader(new Preloader.StateChangeNotification(null));

        ResourceBundle mainWindowBundle = ResourceBundle.getBundle("bundles.MainWindow", ctx.getLocale());
        FXMLLoader fxmlLoader = new FXMLLoader(EBookMain.class.getResource("fxml/main-window.fxml"));
        fxmlLoader.setResources(mainWindowBundle);
        ctx.setMainScene(new Scene(fxmlLoader.load(), ctx.getGc().getScreenWidth(), ctx.getGc().getScreenHeight()));
        ctx.getMainScene().getStylesheets().add(EBookMain.class.getResource("css/global.css").toExternalForm());
        stage.setTitle(mainWindowBundle.getString("title"));
        stage.setScene(ctx.getMainScene());
        stage.setMaximized(true);
        MainWindowController mainWindowController = fxmlLoader.getController();
        ctx.setMainWindowController(mainWindowController);
        mainWindowController.setCtx(ctx);

        stage.show();

    }

    public void showBook(File file) {

    }
}
