package pt.isec.deis.lei.pd.trabprat.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;

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
    private AnchorPane UsersOnline;
    @FXML
    private VBox vboxChannel;
    @FXML
    private VBox vboxDM;
    @FXML
    private VBox vboxUserOnline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        for (int i = 0; i < App.CL_CFG.ChannelsList.size(); i++) {
            Button button = new Button();
            double db = vboxChannel.getMaxWidth();
            button.setMinWidth(db);
            button.setMaxWidth(db);
            button.setText(App.CL_CFG.ChannelsList.get(i).getCName());
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    buttonChannels((Button) t.getSource());
                }
            });
            vboxChannel.getChildren().add(button);
        }
        for (int i = 0; i < App.CL_CFG.OnlineUsers.size(); i++) {
            Button button = new Button();
            double db = vboxUserOnline.getMaxWidth();
            button.setMinWidth(db);
            button.setMaxWidth(db);
            button.setText(App.CL_CFG.OnlineUsers.get(i).getUName());
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    buttonUsersOnline((Button) t.getSource());
                }
            });
            vboxUserOnline.getChildren().add(button);
        }
    }

    public void buttonChannels(Button button) {
        try {
            String ChannelName = button.getText();
            for (int i = 0; i < App.CL_CFG.ChannelsList.size(); i++) {
                if (ChannelName.equals(App.CL_CFG.ChannelsList.get(i).getCName())) {
                    boolean bool = ClientDialog.ShowDialog2(App.CL_CFG.ChannelsList.get(i));
                    if (!bool) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel Password", "Password is invalid!");
                    } else {
                        ServerController.ChannelMessages(ChannelName);
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel", ex.getMessage());
        }

    }

    public void buttonUsersOnline(Button button) {

    }
}
