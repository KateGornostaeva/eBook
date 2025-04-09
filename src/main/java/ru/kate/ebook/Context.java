package ru.kate.ebook;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import lombok.Data;
import ru.kate.ebook.configuration.*;
import ru.kate.ebook.controllers.MainWindowController;
import ru.kate.ebook.localStore.LocalStore;
import ru.kate.ebook.network.Network;

import java.util.Locale;

@Data
public class Context {

    private final EBookMain eBookMain;

    private LocalStore localStore;
    private Role role = Role.ROLE_TEACHER;
    //private Role role = Role.ROLE_STUDENT;

    private Scene mainScene;
    private MainWindowController mainWindowController;
    private WebView webView;

    private final Configurator configurator;
    private Network network;
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
        network = new Network(nc);
        locale = Locale.of(mc.getLocale());
        localStore = new LocalStore();
    }
}
