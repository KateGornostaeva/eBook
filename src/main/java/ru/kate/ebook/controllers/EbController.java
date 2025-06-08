package ru.kate.ebook.controllers;

import javafx.fxml.Initializable;
import lombok.Setter;
import ru.kate.ebook.Context;

/**
 * абстрактный класс, что бы во всех контролерах можно было хранить ссылку на контекст
 */
public abstract class EbController implements Initializable {

    @Setter
    protected Context ctx;
}
