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
        fxmlLoader = new FXMLLoader(EBookMain.class.getResource("fxml/" + fxmlName + ".fxml"));
        setScene(new Scene(fxmlLoader.load()));
        getScene().getStylesheets().add(EBookMain.class.getResource("css/global.css").toExternalForm());
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(ctx.getMainScene().getWindow());
        setTitle(fxmlName);
        controller = fxmlLoader.getController();
        controller.setCtx(ctx);
    }
}
