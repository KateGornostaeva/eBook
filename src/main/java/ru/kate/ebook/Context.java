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
    //private Role role = Role.ROLE_TEACHER;
    //private Role role = Role.ROLE_STUDENT;
    private Role role = Role.ROLE_GUEST; // Роль по умолчанию при запуске

    private Scene mainScene; // главная сцена к которой привязываются диалоги
    private MainWindowController mainWindowController; // главный контролер, через эту ссылку доступ к функциям контролера из других контролеров
    private WebView webView; // главное окно отображения книги (фактически встроенный браузер)

    private final Configurator configurator;
    private Network network;
    private Locale locale; // на будущее для мультиязычности
    private MainConfig mc; // тоже на будущее различные конфигурации
    private NetworkConfig nc; // здесь настройки сети, адрес сервера
    private GraphicsConfig gc; // на будущее

    private boolean connected = false; // признак подключения к серверу

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
