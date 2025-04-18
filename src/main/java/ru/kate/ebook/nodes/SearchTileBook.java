package ru.kate.ebook.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import ru.kate.ebook.controllers.MainWindowController;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.localStore.BookMeta;
import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class SearchTileBook extends Button {

    private final BookMeta meta;

    public SearchTileBook(BookMeta meta, MainWindowController controller) {
        super(meta.getTitle());
        this.meta = meta;
        ImageView imageView = new ImageView(meta.getCover());
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(64);
        setGraphic(imageView);
        setAlignment(Pos.BASELINE_LEFT);
        setOnMouseClicked(e -> {
            try {
                File downloadedZipFile = controller.getCtx().getNetwork().downloadZipFile(meta.getId());
                BookMeta bookMeta = ZipBook.getBookMeta(downloadedZipFile);
                File bookFile = ZipBook.getBookFile(bookMeta);
                controller.readMode(bookFile, bookMeta);
            } catch (URISyntaxException | IOException | InterruptedException | NotSupportedExtension | SQLException |
                     WrongFileFormat ex) {
                throw new RuntimeException(ex);
            }
        });

    }
}
