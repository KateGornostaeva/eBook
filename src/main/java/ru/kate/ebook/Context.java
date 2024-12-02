package ru.kate.ebook;

import javafx.scene.Scene;
import lombok.Data;
import ru.kate.ebook.configuration.Configurator;
import ru.kate.ebook.configuration.GraphicsConfig;
import ru.kate.ebook.configuration.MainConfig;
import ru.kate.ebook.configuration.NetworkConfig;

import java.sql.Statement;
import java.util.Locale;

@Data
public class Context {

    private final EBookMain eBookMain;
    private Statement statementBook;

    private Scene mainScene;
    //private JcModal mainDialog;

    private final Configurator configurator;
    //private Network network;
    private Locale locale;
    private MainConfig mc;
    private NetworkConfig nc;
    private GraphicsConfig gc;

    private boolean connected = false;

    public Context(EBookMain eBookMain) {
        this.eBookMain = eBookMain;
        configurator = new Configurator();
        mc = configurator.getMainConfig();
        nc = configurator.getNetworkConfig();
        gc = configurator.getGraphicsConfig();
        //network = new Network(nc);
        locale = Locale.of(mc.getLocale());
    }
}
