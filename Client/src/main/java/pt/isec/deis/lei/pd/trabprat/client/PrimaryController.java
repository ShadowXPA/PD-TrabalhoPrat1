package pt.isec.deis.lei.pd.trabprat.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class PrimaryController implements Initializable {

    @FXML
    private AnchorPane ChannelList;
    @FXML
    private AnchorPane DirectMessageList;
    @FXML
    private AnchorPane MF;
    @FXML
    private TextField TFMessage;
    @FXML
    private Button btnFile;
    @FXML
    private Button btnSend;
    @FXML
    private AnchorPane Info;
    @FXML
    private AnchorPane UsersOnline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
