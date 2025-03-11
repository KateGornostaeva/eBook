package ru.kate.ebook.nodes;

import javafx.scene.control.Button;
import lombok.Getter;
import ru.kate.ebook.localStore.BookMeta;

public class Tail extends Button {

    @Getter
    private BookMeta meta;

    public Tail() {
        super();
    }

    public Tail(BookMeta meta) {
        super();
        this.meta = meta;
    }
}
