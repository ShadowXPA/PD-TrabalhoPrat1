package pt.isec.deis.lei.pd.trabprat.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.deis.lei.pd.trabprat.client.App;

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
    @FXML
    private VBox vboxChannel;
    @FXML
    private VBox vboxDM;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        for(int i =0;i < App.CL_CFG.ChannelsList.size(); i++){
            Button button = new Button();
            double db = vboxChannel.getMaxWidth();
            button.setMinWidth(db);
            button.setMaxWidth(db);
            button.setText(App.CL_CFG.ChannelsList.get(i).getCName());
            vboxChannel.getChildren().add(button);
        }
        for(int i =0;i < App.CL_CFG.OnlineUsers.size(); i++){
            Button button = new Button();
            double db = UsersOnline.getMaxWidth();
            button.setMinWidth(db);
            button.setMaxWidth(db);
            button.setText(App.CL_CFG.OnlineUsers.get(i).getUName());
            UsersOnline.getChildren().add(button);
        }
    }
}
