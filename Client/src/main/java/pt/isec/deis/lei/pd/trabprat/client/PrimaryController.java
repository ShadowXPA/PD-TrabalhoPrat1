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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;

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
    private VBox vboxChannel;
    @FXML
    private VBox vboxDM;
    @FXML
    private VBox vboxUserOnline;
    @FXML
    private VBox Channel_DM_Info;
    @FXML
    private VBox VBox_Mess_Files;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private ScrollPane sp_channel;
    @FXML
    private ScrollPane sp_DM;
    @FXML
    private ScrollPane sp_info;
    @FXML
    private ScrollPane sp_users;

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
                Messages();
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
            label_description.setWrapText(true);
            label_description.setText(channel.getCDescription());
            Channel_DM_Info.getChildren().add(label_description);
            label_num_users.setText("Number of users: " + String.valueOf(num_users));
            Channel_DM_Info.getChildren().add(label_num_users);
            synchronized (App.CL_CFG) {
                App.CL_CFG.wait(5000);
            }
            int[] array = App.CL_CFG.GetNumMesagesAndFiles();
            label_num_messages.setText("Number of messages: " + String.valueOf(array[0]));
            label_num_files.setText("Number of files: " + String.valueOf(array[1]));
            Channel_DM_Info.getChildren().add(label_num_messages);
            Channel_DM_Info.getChildren().add(label_num_files);

        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void Messages() {
        VBox_Mess_Files.getChildren().removeAll(VBox_Mess_Files.getChildren());
        for (int i = 0; i < App.CL_CFG.ChannelMessage.size(); i++) {
            if (App.CL_CFG.ChannelMessage.get(i).getMID().getMPath() == null) {
                Label label_name = new Label();
                Label label_text_message = new Label();
                Label label_date = new Label();
                label_name.setText(App.CL_CFG.ChannelMessage.get(i).getMID().getMUID().getUName());
                label_date.setText(App.CL_CFG.ChannelMessage.get(i).getMID().getDate().toString());
                label_text_message.setText(App.CL_CFG.ChannelMessage.get(i).getMID().getMText());
                label_text_message.setWrapText(true);
                VBox_Mess_Files.getChildren().add(label_name);
                VBox_Mess_Files.getChildren().add(label_date);
                VBox_Mess_Files.getChildren().add(label_text_message);
            } else {
                Button button = new Button();
                double db = VBox_Mess_Files.getMaxWidth() / 4.0;
                button.setMinWidth(db);
                button.setMaxWidth(db);
                button.setText(App.CL_CFG.ChannelMessage.get(i).getMID().getMText());
                button.setId("" + App.CL_CFG.ChannelMessage.get(i).getMID().getMID());
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        filesSearch((Button) t.getSource());
                    }
                });
                VBox_Mess_Files.getChildren().add(button);
            }
        }
        sp_main.setContent(VBox_Mess_Files);
    }

    public void filesSearch(Button button) {
        try {
            TMessage m = App.CL_CFG.GetMessageByID(Integer.parseInt(button.getId()));
            ServerController.GetFile(m);
        } catch (IOException ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t download the file!");
        }
    }
}
