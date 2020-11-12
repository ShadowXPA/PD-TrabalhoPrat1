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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;

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
    @FXML
    private VBox Channel_DM_Info;
    @FXML
    private VBox VBox_Mess_Files;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        VBox_ChannelList();
        VBox_DMUsers();
        VBox_UsersOnline();
    }

    public void VBox_ChannelList() {
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
    }

    public void VBox_DMUsers() {
        for (int i = 0; i < App.CL_CFG.DMUsers.size(); i++) {
            Button button = new Button();
            double db = vboxDM.getMaxWidth();
            button.setMinWidth(db);
            button.setMaxWidth(db);
            button.setText(App.CL_CFG.DMUsers.get(i).getUName());
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    buttonDMUsers((Button) t.getSource());
                }
            });
            vboxDM.getChildren().add(button);
        }
    }

    public void VBox_UsersOnline() {
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
            var channel = App.CL_CFG.GetChannelByCName(ChannelName);
            boolean bool = ClientDialog.ShowDialog2(channel);
            if (!bool) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel Password", "Password is invalid!");
            } else {
                ServerController.ChannelMessages(ChannelName);
                InfoChannel(channel);
            }
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel", ex.getMessage());
        }
    }

    public void buttonUsersOnline(Button button) {

    }

    public void buttonDMUsers(Button button) {

    }

    public void InfoChannel(TChannel channel) {
        Label label_description = new Label();
        Label label_num_users = new Label();
        Label label_num_messages = new Label();
        Label label_num_files = new Label();
        //TODO -> número de mensagens enviadas e número de ficheiros partilhados.
        try {
            int num_users = 0;
            for (int i = 0; i < App.CL_CFG.ChannelUsers.size(); i++) {
                if (App.CL_CFG.ChannelUsers.get(i).getCID().equals(channel)) {
                    num_users++;
                }
            }
            Channel_DM_Info.getChildren().removeAll(Channel_DM_Info.getChildren());

            label_description.setText(channel.getCDescription());
            Channel_DM_Info.getChildren().add(label_description);

            label_num_users.setText(String.valueOf(num_users));
            Channel_DM_Info.getChildren().add(label_num_users);

            synchronized (App.CL_CFG) {
                App.CL_CFG.wait(5000);
            }
            int[] array = App.CL_CFG.GetNumMesagesAndFiles();
            label_num_messages.setText(String.valueOf(array[0]));
            label_num_files.setText(String.valueOf(array[1]));

        } catch (Exception ex) {
            ex.getMessage();
        }
    }
}
