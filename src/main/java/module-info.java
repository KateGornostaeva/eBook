module ru.kate.ebook.ebook {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.web;
    requires org.slf4j;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires fb2parser;


    opens ru.kate.ebook to javafx.fxml;
    exports ru.kate.ebook;
}