package ru.kate.ebook.controllers;

import javafx.fxml.Initializable;
import lombok.Setter;
import ru.kate.ebook.Context;

public abstract class EbController implements Initializable {

    @Setter
    protected Context ctx;
}
