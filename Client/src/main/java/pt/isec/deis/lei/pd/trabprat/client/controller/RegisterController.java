/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.isec.deis.lei.pd.trabprat.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.validation.Validator;

/**
 * FXML Controller class
 *
 * @author
 */
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void BrowsePhoto(ActionEvent event) {
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
        
        //Validator.PasswordEquals(Password, ConfirmPassword);
        //ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", "Password Error", "The passwords are not equal!");

        try {
            ServerController.Register(new TUser(0, name, Username, Password, null, 0));
        } catch (IOException ex) {
            ClientDialog.ShowDialog(AlertType.ERROR, "Error Dialog", null, ex.getMessage());
        }
    }

}
