package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import ru.kate.ebook.Context;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.network.ProfileDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileDialogController extends EbController {
    @FXML
    public Label role;
    @FXML
    public Label family;
    @FXML
    public Label name;
    @FXML
    public Label patronym;
    @FXML
    public Label email;

    @SneakyThrows
    @Override
    public void setCtx(Context ctx) {
        super.setCtx(ctx);

        role.setText("  " + ctx.getRole().getValue());

        ProfileDto profileDto = ctx.getNetwork().getProfile();
        family.setText(profileDto.getLastName());
        name.setText(profileDto.getName());
        patronym.setText(profileDto.getMiddleName());
        email.setText(profileDto.getEmail());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void exitHandler(ActionEvent actionEvent) throws IOException, URISyntaxException, InterruptedException {
        role.getScene().getWindow().hide();
        ctx.setRole(Role.ROLE_GUEST);
        ctx.setConnected(false);
        ctx.getMainWindowController().drawMainPane();
        Button btnUser = (Button) ctx.getMainScene().lookup("#btnUser");
        btnUser.setGraphic(null);
        btnUser.setText("Войти");
        /*EbModal authDialog = new EbModal(null, "auth-dialog", ctx);
        AuthDialogController controller = (AuthDialogController) authDialog.getController();
        controller.setStage(authDialog);
        authDialog.show();*/
    }
}
