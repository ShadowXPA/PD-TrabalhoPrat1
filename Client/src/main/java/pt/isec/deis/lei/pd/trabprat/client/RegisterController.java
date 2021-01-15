package pt.isec.deis.lei.pd.trabprat.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.validation.Validator;

public class RegisterController implements Initializable {

    @FXML
    private TextField TFName;
    @FXML
    private TextField TFUsername;
    @FXML
    private PasswordField PFPassword;
    @FXML
    private PasswordField PFConfirmPassword;
    @FXML
    private Button BtnPhoto;
    @FXML
    private Button BtnCancel;
    @FXML
    private Button BtnLogin;
    @FXML
    private TextField TFPhoto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void BrowsePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        File file = fileChooser.showOpenDialog(App.CL_CFG.Stage);
        TFPhoto.setText(file.getAbsolutePath());
    }

    @FXML
    private void CancelCreateAccount(ActionEvent event) throws IOException {
        App.CL_CFG.Stage.setWidth(DefaultWindowSizes.DEFAULT_LOGIN_WIDTH);
        App.CL_CFG.Stage.setHeight(DefaultWindowSizes.DEFAULT_LOGIN_HEIGHT);
        App.setRoot("Login");
    }

    @FXML
    private void RegisterAccount(ActionEvent event) {
        String name = TFName.getText();
        String Username = TFUsername.getText();
        String Password = PFPassword.getText();
        String ConfirmPassword = PFConfirmPassword.getText();
        String Path = TFPhoto.getText().replace("\\", "\\\\");
        boolean bool = true;
        if (!Validator.Name(name)) {
            bool = false;
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Name Error", "The name is invalid!");
            TFName.setStyle("-fx-border-color: red");
        } else {
            TFName.setStyle("-fx-border-color: none");
        }
        if (!Validator.Username(Username)) {
            bool = false;
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Username Error", "The username is invalid!");
            TFUsername.setStyle("-fx-border-color: red");
        } else {
            TFUsername.setStyle("-fx-border-color: none");
        }
        if (!Validator.PasswordEquals(Password, ConfirmPassword)) {
            bool = false;
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Password Error", "The passwords are not equal!");
            PFPassword.setStyle("-fx-border-color: red");
            PFConfirmPassword.setStyle("-fx-border-color: red");
        } else {
            PFPassword.setStyle("-fx-border-color: none");
            PFConfirmPassword.setStyle("-fx-border-color: none");
        }
        if (!Validator.Password(Password)) {
            bool = false;
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Password Error", "The passwords need to have one upper case letter, one small case letter, one number and a minimum of 6 characters!");
            PFPassword.setStyle("-fx-border-color: red");
        } else {
            PFPassword.setStyle("-fx-border-color: none");
        }
        if (Path.isEmpty()) {
            bool = false;
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Photo Error", "The photo is incorrect!");
        }
        //bool serve para verificar se as validaçoes estao corretas
        if (bool) {
            Thread td = new Thread(() -> {
                try {
                    // enviar o novo utilizador
                    String PasswordEncripted = AES.Encrypt(Password);
                    ServerController.Register(new TUser(0, name, Username, PasswordEncripted, Path, 0));
                } catch (Exception ex) {
                    ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", null, ex.getMessage());
                }
            });
            td.setDaemon(true);
            td.start();
            try {
                App.CL_CFG.Stage.setWidth(DefaultWindowSizes.DEFAULT_LOGIN_WIDTH);
                App.CL_CFG.Stage.setHeight(DefaultWindowSizes.DEFAULT_LOGIN_HEIGHT);
                App.setRoot("Login");
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
    }
}
