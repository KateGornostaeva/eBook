module ru.kate.ebook.ebook {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;


    opens ru.kate.ebook to javafx.fxml;
    exports ru.kate.ebook;
}