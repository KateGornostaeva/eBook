package ru.kate.ebook;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kate.ebook.controllers.MainWindowController;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Paths;
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
        try {
            PrintStream fileOut = new PrintStream("./out2.txt");
            System.setOut(fileOut);
            System.setErr(fileOut);

            Thread.sleep(1000);
            this.notifyPreloader(new Preloader.StateChangeNotification(null));

            ResourceBundle mainWindowBundle = ResourceBundle.getBundle("bundles.MainWindow", ctx.getLocale());
            String path = getClass().getResource("fxml/main-window.fxml").getPath().toString().substring(1).replace("ru.kate.ebook.ebook", "");
            FXMLLoader fxmlLoader = new FXMLLoader(Paths.get(path).toUri().toURL());
            fxmlLoader.setResources(mainWindowBundle);
            ctx.setMainScene(new Scene(fxmlLoader.load(), ctx.getGc().getScreenWidth(), ctx.getGc().getScreenHeight()));
            ctx.getMainScene().getStylesheets().add(getClass().getResource("css/global.css").toExternalForm());
            stage.setTitle(mainWindowBundle.getString("title"));
            stage.setScene(ctx.getMainScene());
            stage.setMaximized(true);
            MainWindowController mainWindowController = fxmlLoader.getController();
            ctx.setMainWindowController(mainWindowController);
            mainWindowController.setCtx(ctx);
            stage.show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
