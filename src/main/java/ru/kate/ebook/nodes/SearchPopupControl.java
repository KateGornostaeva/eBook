package ru.kate.ebook.nodes;

import javafx.scene.control.PopupControl;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.kate.ebook.controllers.MainWindowController;
import ru.kate.ebook.localStore.BookMeta;

import java.util.List;

public class SearchPopupControl extends PopupControl {

    public SearchPopupControl(List<BookMeta> metas, MainWindowController controller, double width) {
        super();
        init(metas, controller, width);
    }

    private void init(List<BookMeta> metas, MainWindowController controller, double width) {
        setAutoHide(true);
        setAutoFix(true);
        VBox vBox = new VBox();
        for (BookMeta meta : metas) {
            SearchTileBook searchTileBook = new SearchTileBook(meta, controller);
            searchTileBook.setPrefWidth(width);
            searchTileBook.setStyle("-fx-background-color: #6699994D;");
            VBox.setVgrow(searchTileBook, Priority.ALWAYS);
            vBox.getChildren().add(searchTileBook);
        }
        getScene().setRoot(vBox);

    }

}
