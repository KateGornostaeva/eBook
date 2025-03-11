package ru.kate.ebook.nodes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import ru.kate.ebook.Context;
import ru.kate.ebook.EBookMain;
import ru.kate.ebook.controllers.EbController;

import java.io.IOException;

public class EbModal extends Stage {

    @Getter
    private final EbController controller;

    @Getter
    private final FXMLLoader fxmlLoader;

    public EbModal(String bundleName, String fxmlName, Context ctx) throws IOException {
        super();
        //ResourceBundle enterDialogBundle = ResourceBundle.getBundle("bundles." + bundleName, ctx.getLocale());
        fxmlLoader = new FXMLLoader(EBookMain.class.getResource("fxml/" + fxmlName + ".fxml"));
        //fxmlLoader.setResources(enterDialogBundle);
        setScene(new Scene(fxmlLoader.load()));
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(ctx.getMainScene().getWindow());
        setTitle(fxmlName);
        controller = fxmlLoader.getController();
        controller.setCtx(ctx);
    }
}
