package pt.isec.deis.lei.pd.trabprat.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void LoginAccount(ActionEvent event) throws IOException {

    }

    @FXML
    private void RegisterAccount(ActionEvent event) throws IOException {
        App.CL_CFG.Stage.setWidth(DefaultWindowSizes.DEFAULT_REGISTER_WIDTH);
        App.CL_CFG.Stage.setHeight(DefaultWindowSizes.DEFAULT_REGISTER_HEIGHT);
        App.setRoot("Register");
    }

}
