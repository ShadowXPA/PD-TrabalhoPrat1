package pt.isec.deis.lei.pd.trabprat.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class PrimaryController implements Initializable {

    @FXML
    private TextField TFMessage;
    @FXML
    private Button btnFile;
    @FXML
    private Button btnSend;
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
        App.CL_CFG.SelectedChannel = null;
        ScrollPanes();
        VBox_ChannelList();
        VBox_DMUsers();
        VBox_UsersOnline();
    }

    public void ScrollPanes() {
        sp_main.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_main.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp_channel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_channel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp_DM.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_DM.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp_info.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_info.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp_users.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp_users.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp_main.vvalueProperty().bind(VBox_Mess_Files.heightProperty());
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
        sp_channel.setContent(vboxChannel);
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
        sp_DM.setContent(vboxDM);
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
        sp_users.setContent(vboxUserOnline);
    }

    public void buttonChannels(Button button) {
        try {
            String ChannelName = button.getText();
            var channel = App.CL_CFG.GetChannelByCName(ChannelName);
            App.CL_CFG.SelectedChannel = channel;
            boolean bool = ClientDialog.ShowDialog2(channel);
            if (!bool) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel Password", "Password is invalid!");
            } else {
                ServerController.ChannelMessages();
                InfoChannel(channel);
                Messages(true);
            }
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel", ex.getMessage());
        }
    }

    public void buttonDMUsers(Button button) {
        try {
            String DMChannel = button.getText();
            var channel = App.CL_CFG.GetDMByUName(DMChannel);
            App.CL_CFG.SelectedChannel = channel;
            ServerController.ChannelMessages();
            InfoChannel(channel);
            Messages(false);
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel", ex.getMessage());
        }
    }

    public void buttonUsersOnline(Button button) {

    }

    public void InfoChannel(Object channel) {
        Label label_description = new Label();
        Label label_num_users = new Label();
        Label label_num_messages = new Label();
        Label label_num_files = new Label();
        try {
            Channel_DM_Info.getChildren().removeAll(Channel_DM_Info.getChildren());
            if (channel instanceof TChannel) {
                int num_users = 0;
                for (int i = 0; i < App.CL_CFG.ChannelUsers.size(); i++) {
                    if (App.CL_CFG.ChannelUsers.get(i).getCID().equals(channel)) {
                        num_users++;
                    }
                }
                label_description.setWrapText(true);
                label_description.setText(((TChannel) channel).getCDescription());
                Channel_DM_Info.getChildren().add(label_description);
                label_num_users.setText("Number of users: " + String.valueOf(num_users));
                Channel_DM_Info.getChildren().add(label_num_users);
            }
            synchronized (App.CL_CFG.LockCM) {
                App.CL_CFG.LockCM.wait(1000);
            }
            int[] array;
            if (channel instanceof TChannel) {
                array = App.CL_CFG.GetNumMesagesAndFiles();
            } else {
                array = App.CL_CFG.GetNumMesagesAndFilesDM();
            }
            label_num_messages.setText("Number of messages: " + String.valueOf(array[0]));
            label_num_files.setText("Number of files: " + String.valueOf(array[1]));
            Channel_DM_Info.getChildren().add(label_num_messages);
            Channel_DM_Info.getChildren().add(label_num_files);
            if (App.CL_CFG.SelectedChannel instanceof TChannel && ((TChannel) App.CL_CFG.SelectedChannel).getCUID().equals(App.CL_CFG.MyUser)) {
                Button EditChannel = new Button("Edit Channel");
                EditChannel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        EditChannel((Button) t.getSource());
                    }
                });
                Button DeleteChannel = new Button("Delete Channel");
                DeleteChannel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        DeleteChannel((Button) t.getSource());
                    }
                });
                Channel_DM_Info.getChildren().add(EditChannel);
                Channel_DM_Info.getChildren().add(DeleteChannel);
            }
            sp_info.setContent(Channel_DM_Info);
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void EditChannel(Button button) {

    }

    public void DeleteChannel(Button button) {

    }

    public void Messages(boolean bool) {
        //TODO WRAPTEXT
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            VBox_Mess_Files.getChildren().removeAll(VBox_Mess_Files.getChildren());
            ArrayList<?> obj;
            if (bool) {
                obj = App.CL_CFG.ChannelMessage;
            } else {
                obj = App.CL_CFG.DirectMessages;
            }
            for (int i = 0; i < obj.size(); i++) {
                TMessage msg;
                if (bool) {
                    msg = ((TChannelMessage) obj.get(i)).getMID();
                } else {
                    msg = ((TDirectMessage) obj.get(i)).getMID();
                }
                if (msg.getMPath() == null) {
                    Label label_name = new Label();
                    Label label_text_message = new Label();
                    Label label_date = new Label();
                    Label label_space = new Label();
                    label_text_message.setMinWidth(VBox_Mess_Files.getMaxWidth());
                    label_text_message.setMaxWidth(VBox_Mess_Files.getMaxWidth());
                    label_text_message.setWrapText(true);
                    label_name.setText("Message from: " + msg.getMUID().getUName());
                    label_date.setText("Date: " + msg.getDate().toString());
                    label_text_message.setText("Message: " + msg.getMText());
                    label_space.setText("\n");
                    VBox_Mess_Files.getChildren().add(label_name);
                    VBox_Mess_Files.getChildren().add(label_date);
                    VBox_Mess_Files.getChildren().add(label_text_message);
                    VBox_Mess_Files.getChildren().add(label_space);
                } else {
                    Button button = new Button();
                    Label label_name = new Label();
                    Label label_space = new Label();
                    double db = VBox_Mess_Files.getMaxWidth() / 4.0;
                    button.setMinWidth(db);
                    button.setMaxWidth(db);
                    label_name.setText("File from: " + msg.getMUID().getUName());
                    button.setText(msg.getMText());
                    button.setId("" + msg.getMID());
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            FileDownload((Button) t.getSource());
                        }
                    });
                    label_space.setText("\n");
                    VBox_Mess_Files.getChildren().add(label_name);
                    VBox_Mess_Files.getChildren().add(button);
                    VBox_Mess_Files.getChildren().add(label_space);
                }
            }
            sp_main.setContent(VBox_Mess_Files);
        });

    }

    public void FileDownload(Button button) {
        try {
            TMessage m = App.CL_CFG.GetMessageByID(Integer.parseInt(button.getId()));
            ServerController.GetFile(m);
        } catch (IOException ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t download the file!");
        }
    }

    @FXML
    public void SendMessage(ActionEvent event) {
        if (App.CL_CFG.SelectedChannel == null) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Select Channel", "Select a channel to send a message!");
            return;
        }
        String text_message = TFMessage.getText();
        if (!text_message.isEmpty()) {
            try {
                Object object = App.CL_CFG.SelectedChannel;
                TMessage m = new TMessage(0, App.CL_CFG.MyUser, text_message, null, 0);
                TChannelMessage cm = null;
                TDirectMessage dm = null;
                if (App.CL_CFG.SelectedChannel instanceof TChannel) {
                    cm = new TChannelMessage((TChannel) object, m);
                } else if (App.CL_CFG.SelectedChannel instanceof TUser) {
                    dm = new TDirectMessage(m, (TUser) object);
                }
                final Object obj = cm == null ? dm : cm;
                TFMessage.setText("");
                Thread td = new Thread(() -> {
                    try {
                        ServerController.NewMessage(obj);
                        boolean bool = obj instanceof TChannelMessage;
                        synchronized (App.CL_CFG.LockCM) {
                            App.CL_CFG.LockCM.wait(1000);
                            Messages(bool);
                        }
                    } catch (Exception ex) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t send message!");
                    }
                });
                td.setDaemon(true);
                td.start();
            } catch (Exception ex) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t send message!");
            }
        }
    }

    @FXML
    public void SendFile(ActionEvent event
    ) {
        if (App.CL_CFG.SelectedChannel == null) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Select Channel", "Select a channel to send a file!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the file");
        File file = fileChooser.showOpenDialog(App.CL_CFG.Stage);
        if (file != null) {
            try {
                Object object = App.CL_CFG.SelectedChannel;
                UUID uuid = UUID.randomUUID();
                TMessage m = new TMessage(0, App.CL_CFG.MyUser, file.getName(), uuid.toString(), 0);
                TChannelMessage cm = null;
                TDirectMessage dm = null;
                if (App.CL_CFG.SelectedChannel instanceof TChannel) {
                    cm = new TChannelMessage((TChannel) object, m);
                } else if (App.CL_CFG.SelectedChannel instanceof TUser) {
                    dm = new TDirectMessage(m, (TUser) object);
                }
                final Object obj = cm == null ? dm : cm;
                Thread td = new Thread(() -> {
                    try {
                        ServerController.NewMessage(obj);
                        ServerController.SendFile(file.getAbsolutePath(), App.CL_CFG.MyUser.getUUsername(), uuid);
                        boolean bool = obj instanceof TChannelMessage;
                        synchronized (App.CL_CFG.LockCM) {
                            App.CL_CFG.LockCM.wait(1000);
                            Messages(bool);
                        }
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Info Dialog", "Info File", "File uploaded!");
                    } catch (Exception ex) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t send message!");
                    }
                });
                td.setDaemon(true);
                td.start();
            } catch (Exception ex) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t send message!");
            }
        }
    }
}
