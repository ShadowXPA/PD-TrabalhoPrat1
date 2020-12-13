package pt.isec.deis.lei.pd.trabprat.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.validation.Validator;

public class LoginController implements Initializable {

    @FXML
    private TextField TFUsername;
    @FXML
    private PasswordField PFPassword;
    @FXML
    private Button BtnLogin;
    @FXML
    private Hyperlink HypRegister;
    private static Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void LoginAccount(ActionEvent event) throws IOException {
        try {
            String Username = TFUsername.getText();
            String Password = PFPassword.getText();
            boolean bool = true;

            //usa o validator para verificar o utilizador com REGEX
            if (!Validator.Username(Username)) {
                bool = false;
                ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Username Error", "The username is invalid!");
                TFUsername.setStyle("-fx-border-color: red");
            } else {
                TFUsername.setStyle("-fx-border-color: none");
            }
            //usa o validator para verificar a password com REGEX
            if (!Validator.Password(Password)) {
                bool = false;
                ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Password Error", "The passwords need to have one upper case letter, one small case letter, one number and a minimum of 6 characters!");
                PFPassword.setStyle("-fx-border-color: red");
            } else {
                PFPassword.setStyle("-fx-border-color: none");
            }
            //Encriptação da password
            Password = AES.Encrypt(Password);
            if (bool) {
                boolean accepted = false;
                //Envio do login para o server
                ServerController.Login(new TUser(0, "", Username, Password, "", 0));
                synchronized (App.CL_CFG) {
                    App.CL_CFG.wait();
                    accepted = App.CL_CFG.isLoggedIn();
                }
                //Verifica se o user existe e está tudo correto
                if (accepted) {
                    App.CL_CFG.Stage.setWidth(DefaultWindowSizes.DEFAULT_MAIN_WIDTH);
                    App.CL_CFG.Stage.setHeight(DefaultWindowSizes.DEFAULT_MAIN_HEIGHT);
                    App.setRoot("primary");
                }
            }
        } catch (Exception ex) {
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", null, ex.getMessage());
        }
    }

    @FXML
    private void RegisterAccount(ActionEvent event) throws IOException {
        App.CL_CFG.Stage.setWidth(DefaultWindowSizes.DEFAULT_REGISTER_WIDTH);
        App.CL_CFG.Stage.setHeight(DefaultWindowSizes.DEFAULT_REGISTER_HEIGHT);
        App.setRoot("Register");
    }

    @FXML
    private void OnKeyPressed_pfpassword(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            BtnLogin.fire();
        }
        event.consume();
    }

}
