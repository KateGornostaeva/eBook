package ru.kate.ebook;

import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import lombok.Data;
import ru.kate.ebook.configuration.Configurator;
import ru.kate.ebook.configuration.GraphicsConfig;
import ru.kate.ebook.configuration.MainConfig;
import ru.kate.ebook.configuration.NetworkConfig;
import ru.kate.ebook.etb.Ebook;
import ru.kate.ebook.localStore.LocalStore;
import ru.kate.ebook.network.Network;

import java.util.Locale;

@Data
public class Context {

    private final EBookMain eBookMain;
    private Ebook ebook;

    private LocalStore localStore;

    private Scene mainScene;
    private WebView webView;
    private TreeView<TreeItemBook> treeView;

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
