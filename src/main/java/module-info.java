module ru.kate.ebook.ebook {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.kate.ebook.ebook to javafx.fxml;
    exports ru.kate.ebook.ebook;
}