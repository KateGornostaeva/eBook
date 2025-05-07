package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.exceptions.WrongAuthorisation;
import ru.kate.ebook.nodes.EbModal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AuthDialogController extends EbController {

    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button exitButton;


    @FXML
    private void handleBtnReg(ActionEvent event) throws IOException {
        EbModal authDialog = new EbModal(null, "reg-dialog", ctx);
        authDialog.show();
    }

    @FXML
    private void handleBtnEnter(ActionEvent event) {
        try {
            if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                ctx.getNetwork().login();
            } else {
                ctx.getNetwork().login(txtUserName.getText(), txtPassword.getText());
            }
            ctx.setConnected(true);
            ctx.setRole(ctx.getNetwork().getRole());

            Button btnUser = (Button) ctx.getMainScene().lookup("#btnUser");
            btnUser.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("user.png"))));
            btnUser.setText("");
            //btnUser.setContentDisplay(ContentDisplay.TOP);
            exitButton.getScene().getWindow().hide();
            ctx.getMainWindowController().drawMainPane();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        } catch (WrongAuthorisation e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Username or Password");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
