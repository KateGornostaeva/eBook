package ru.kate.ebook.nodes;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import ru.kate.ebook.configuration.Role;

public class LastOpenedScrollPane extends ScrollPane {

    private boolean grid = true;
    private Role role;

    public LastOpenedScrollPane(boolean grid, Role role) {
        super();
        this.grid = grid;
        this.role = role;
        init();
    }

    private void init() {
        VBox vbox = new VBox();
        this.getChildren().add(vbox);

        Label label = new Label("Недавно открытые");

        if (grid) {
            vbox.getChildren().add(label);

        } else {

        }

    }
}
