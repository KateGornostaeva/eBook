package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.exceptions.WrongAuthorisation;
import ru.kate.ebook.nodes.EbModal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер привязанный к диалогу авторизации
 */
@Slf4j
public class AuthDialogController extends EbController {

    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnEnter;

    @Setter
    private Stage stage;


    @FXML
    private void handleBtnReg(ActionEvent event) throws IOException {
        EbModal regDialog = new EbModal(null, "reg-dialog", ctx);
        regDialog.show();
        stage.close();
    }

    private void processBtnEnter(ActionEvent event) {
        try {
            if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                ctx.getNetwork().login();
            } else {
                ctx.getNetwork().login(txtUserName.getText(), txtPassword.getText());
            }
            ctx.setConnected(true);
            ctx.setRole(ctx.getNetwork().getRole());

            if (ctx.getRole().equals(Role.ROLE_TEACHER)) {
                // высвечивам кнопку для учителя
                Button btnDraftAndPublished = (Button) ctx.getMainScene().lookup("#btnDraftAndPublished");
                btnDraftAndPublished.setVisible(true);
            }

            // меняем кнопку Войти на кнопку профиля
            Button btnUser = (Button) ctx.getMainScene().lookup("#btnUser");
            btnUser.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("user.png"))));
            btnUser.setText("");
            ctx.getMainWindowController().drawMainPane();
            stage.close();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (WrongAuthorisation e) {
            // если не смогла войти на сервер, то показываю окошко с предупреждением
            event.consume();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initOwner(ctx.getMainScene().getWindow());
            dialog.getDialogPane().setStyle("-fx-background-color: #9584E0;");
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(25));
            vBox.setSpacing(35);
            Text text = new Text("Не правильное имя или пароль");
            Text text1 = new Text("Попробуйте ещё раз");
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(25);
            Button btnOk = new Button("  OK  ");
            btnOk.setPrefWidth(200);
            btnOk.setStyle("-fx-background-color: #554BA3; -fx-text-fill: white");
            btnOk.setOnAction(event1 -> {
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                dialog.close();
            });
            hBox.getChildren().addAll(btnOk);
            vBox.getChildren().addAll(text, text1, hBox);
            dialog.getDialogPane().setContent(vBox);
            dialog.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // это что бы после ввода пароля реагировало на Энтер
        txtPassword.setOnAction(actionEvent -> {
            processBtnEnter(actionEvent);
        });
// перехват закрытия диалога, что бы при возврате из регистрации, диалог авторизации был на месте
        btnEnter.addEventFilter(ActionEvent.ACTION, event -> {
            processBtnEnter(event);
            event.consume();
        });
    }
}
