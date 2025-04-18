package ru.kate.ebook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import ru.kate.ebook.network.SignUpRequestDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegDialogController extends EbController {

    @FXML
    public TextField txtLastName;

    @FXML
    public TextField txtName;

    @FXML
    public TextField txtMiddleName;

    @FXML
    public TextField txtEmail;

    @FXML
    public TextField txtLogin;

    @FXML
    public TextField txtPassword;

    @FXML
    public TextField txtRepeatPassword;

    @FXML
    public ChoiceBox chbRole;

    public void handleBtnReg(ActionEvent actionEvent) throws URISyntaxException, IOException, InterruptedException {
        if (!txtLastName.getText().isEmpty()
                && !txtName.getText().isEmpty()
                && !txtEmail.getText().isEmpty()
                && !txtLogin.getText().isEmpty()
                && !txtPassword.getText().isEmpty()
                && !txtRepeatPassword.getText().isEmpty()
                && txtPassword.getText().equals(txtRepeatPassword.getText())) {

            SignUpRequestDto dto = new SignUpRequestDto();
            dto.setLastName(txtLastName.getText());
            dto.setName(txtName.getText());
            dto.setMiddleName(txtMiddleName.getText());
            dto.setEmail(txtEmail.getText());
            dto.setLogin(txtLogin.getText());
            dto.setPassword(txtPassword.getText());
            dto.setRole(chbRole.getValue().equals("Преподаватель") ? "ROLE_TEACHER" : "ROLE_STUDENT");
            ctx.getNetwork().signUp(dto);
            txtLastName.getScene().getWindow().hide();

        } else {
            //показываем всплывашку алерт
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chbRole.getItems().addAll("Студент", "Преподаватель");
        chbRole.setValue("Студент");
    }

    private void showOkResult() {

    }
}
