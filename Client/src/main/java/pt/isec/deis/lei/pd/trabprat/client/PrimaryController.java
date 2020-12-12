package pt.isec.deis.lei.pd.trabprat.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
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
    @FXML
    private MenuItem MI_Send_Message;
    @FXML
    private MenuItem MI_Send_File;
    @FXML
    private MenuItem MI_Add_Channel;
    @FXML
    private MenuItem MI_Search_Users;
    @FXML
    private MenuItem MI_About;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread[] threads = new Thread[]{
            new Thread(() -> {
                TdChannel();
            }),
            new Thread(() -> {
                TdMessages();
            }),
            new Thread(() -> {
                TdDM();
            }),
            new Thread(() -> {
                TdOnlineUsers();
            })
        };
        for (int i = 0; i < threads.length; i++) {
            threads[i].setName("UI Thread " + i);
            threads[i].setDaemon(true);
            threads[i].start();
        }
        App.CL_CFG.SelectedChannel = null;
        ScrollPanes();
        VBox_ChannelList();
        VBox_DMUsers();
        VBox_UsersOnline();
    }

    public void TdChannel() {
        while (true) {
            synchronized (App.CL_CFG.LockCL) {
                try {
                    App.CL_CFG.LockCL.wait();
                    VBox_ChannelList();
                    InfoChannel(App.CL_CFG.SelectedChannel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void TdMessages() {
        while (true) {
            synchronized (App.CL_CFG.LockCM) {
                try {
                    App.CL_CFG.LockCM.wait();
                    Object obj = App.CL_CFG.SelectedChannel;
                    boolean bool = obj instanceof TChannel;
                    InfoChannel(obj);
                    Messages(bool);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void TdDM() {
        while (true) {
            synchronized (App.CL_CFG.LockDMUsers) {
                try {
                    App.CL_CFG.LockDMUsers.wait();
                    VBox_DMUsers();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void TdOnlineUsers() {
        while (true) {
            synchronized (App.CL_CFG.LockOUsers) {
                try {
                    App.CL_CFG.LockOUsers.wait();
                    VBox_UsersOnline();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
        Platform.runLater(() -> {
            vboxChannel.getChildren().removeAll(vboxChannel.getChildren());
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
        });
    }

    public void VBox_DMUsers() {
        Platform.runLater(() -> {
            vboxDM.getChildren().removeAll(vboxDM.getChildren());
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
        });
    }

    public void VBox_UsersOnline() {
        Platform.runLater(() -> {
            vboxUserOnline.getChildren().removeAll(vboxUserOnline.getChildren());
            for (int i = 0; i < App.CL_CFG.OnlineUsers.size(); i++) {
                Button button = new Button();
                double db = vboxUserOnline.getMaxWidth();
                button.setMinWidth(db);
                button.setMaxWidth(db);
                button.setText(App.CL_CFG.OnlineUsers.get(i).getUName());
                vboxUserOnline.getChildren().add(button);
            }
            sp_users.setContent(vboxUserOnline);
        });
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
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error", "Channel", ex.getMessage());
        }
    }

    public void InfoChannel(Object channel) {
        Platform.runLater(() -> {
            Label label_description = new Label();
            Label label_num_users = new Label();
            Label label_num_messages = new Label();
            Label label_num_files = new Label();
            try {
                Channel_DM_Info.getChildren().removeAll(Channel_DM_Info.getChildren());
                if (channel == null) {
                    return;
                }
                if (channel instanceof TChannel) {
                    int num_users = 0;
                    synchronized (App.CL_CFG.LockCU) {
                        for (int i = 0; i < App.CL_CFG.ChannelUsers.size(); i++) {
                            if (App.CL_CFG.ChannelUsers.get(i).getCID().equals(channel)) {
                                num_users++;
                            }
                        }
                    }
                    label_description.setWrapText(true);
                    label_description.setText("Description: " + ((TChannel) channel).getCDescription());
                    Channel_DM_Info.getChildren().add(label_description);
                    label_num_users.setText("Number of users: " + String.valueOf(num_users));
                    Channel_DM_Info.getChildren().add(label_num_users);
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
        });
    }

    public void EditChannel(Button button) {
        try {
            TChannel channel;
            channel = ClientDialog.ShowDialog3(false);
            if (channel == null) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Channel Editing", "Can´t edit channel!");
                return;
            }
            Thread td = new Thread(() -> {
                try {
                    TChannel c = (TChannel) App.CL_CFG.SelectedChannel;
                    ServerController.EditChannel(new TChannelUser(new TChannel(c.getCID(), c.getCUID(), c.getCName(), channel.getCDescription(), channel.getCPassword(), c.getCDate()), App.CL_CFG.MyUser));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            td.setDaemon(true);
            td.start();
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Channel Editing", "Can´t edit channel!");
        }
    }

    public void DeleteChannel(Button button) {
        try {
            TChannel channel;
            boolean bool = ClientDialog.ShowDialog4();
            if (bool) {
                Thread td = new Thread(() -> {
                    try {
                        TChannel c = (TChannel) App.CL_CFG.SelectedChannel;
                        ServerController.DeleteChannel(new TChannelUser(c, App.CL_CFG.MyUser));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                td.setDaemon(true);
                td.start();
            }
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Deleting Channel", "Can´t delete channel!");
        }
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
            if (obj == null) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMMM d, yyyy H:mm");
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
                    label_date.setText("Date: " + sdf.format(msg.getDate()));
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
                    Label label_date = new Label();
                    double db = VBox_Mess_Files.getMaxWidth() / 4.0;
                    button.setMinWidth(db);
                    button.setMaxWidth(db);
                    label_name.setText("File from: " + msg.getMUID().getUName());
                    label_date.setText("Date: " + sdf.format(msg.getDate()));
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
                    VBox_Mess_Files.getChildren().add(label_date);
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
            return;
        }
        String text_message = TFMessage.getText();
        if (!text_message.isEmpty()) {
            try {
                final Object object = App.CL_CFG.SelectedChannel;
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
    public void SendFile(ActionEvent event) {
        if (App.CL_CFG.SelectedChannel == null) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Select Channel", "Select a channel to send a file!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the file");
        File file = fileChooser.showOpenDialog(App.CL_CFG.Stage);
        SendFileToServer(file);
    }

    @FXML
    private void OnDragFile_spmain(DragEvent event) {
        Dragboard drag = event.getDragboard();
        if (drag.hasFiles()) {
            if (App.CL_CFG.SelectedChannel == null) {
                ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Select Channel", "Select a channel to send a file!");
                return;
            }
            SendFileToServer(drag.getFiles().get(0));
        }
        event.consume();
    }

    @FXML
    private void OnKeyPressed_tfmessage(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            btnSend.fire();
        }
        event.consume();
    }

    private void SendFileToServer(File file) {
        if (file != null) {
            try {
                final Object object = App.CL_CFG.SelectedChannel;
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
                        ClientDialog.ShowDialog(Alert.AlertType.INFORMATION, "Info Dialog", "Info File", "File uploaded!");
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
    private void OnDragOverFile_spmain(DragEvent event) {
        if (event.getGestureSource() != sp_main && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    private void SendMessage_menuitem(ActionEvent event) {
        try {
            Pair<String, String> pair = ClientDialog.ShowDialog5();
            if (pair == null) {
                return;
            }
            String message_to = pair.getKey();
            String message_text = pair.getValue();
            TUser user = new TUser(0, null, message_to, null, null, 0);
            TMessage message = new TMessage(0, App.CL_CFG.MyUser, message_text, null, 0);
            TDirectMessage dm = new TDirectMessage(message, user);
            ServerController.NewMessage(dm);
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Sending Message", "Can´t send the message!");
        }
    }

    @FXML
    private void SendFile_menuitem(ActionEvent event) {
        try {
            Pair<String, String> pair = ClientDialog.ShowDialog6();
            if (pair == null) {
                return;
            }
            UUID uuid = UUID.randomUUID();
            String message_to = pair.getKey();
            String file_path = pair.getValue();
            File file = new File(file_path);
            TDirectMessage dm = new TDirectMessage(new TMessage(0, App.CL_CFG.MyUser, file.getName(), uuid.toString(), 0), new TUser(0, null, message_to, null, null, 0));
            Thread td = new Thread(() -> {
                try {
                    ServerController.NewMessage(dm);
                    ServerController.SendFile(file_path, App.CL_CFG.MyUser.getUUsername(), uuid);
                    ClientDialog.ShowDialog(Alert.AlertType.INFORMATION, "Info Dialog", "Info File", "File uploaded!");
                } catch (IOException ex) {
                    ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error File", "Can´t send message!");
                }
            });
            td.setDaemon(true);
            td.start();
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Sending Message", "Can´t send the message!");
        }
    }

    @FXML
    private void AddChannel_menuitem(ActionEvent event) {
        try {
            TChannel channel;
            channel = ClientDialog.ShowDialog3(true);
            if (channel == null) {
                return;
            }
            Thread td = new Thread(() -> {
                try {
                    ServerController.CreateChannel(new TChannel(0, App.CL_CFG.MyUser, channel.getCName(), channel.getCDescription(), channel.getCPassword(), 0));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            td.setDaemon(true);
            td.start();
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Channel Creation", "Can´t create the new channel!");
        }
    }

    @FXML
    private void SearchUsers_menuitem(ActionEvent event) {
        try {
            String str = ClientDialog.ShowDialog7();
            if (str == null) {
                return;
            }
            Thread td = new Thread(() -> {
                try {
                    ServerController.SearchUser(str);
                    synchronized (App.CL_CFG.LockFo) {
                        App.CL_CFG.LockFo.wait();
                        ClientDialog.ShowDialog8(App.CL_CFG.FoundUsers);
                        App.CL_CFG.FoundUsers = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            td.setDaemon(true);
            td.start();
        } catch (Exception ex) {
            ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error Search User", "Can´t search the user!");
        }
    }

    @FXML
    private void About_menuitem(ActionEvent event) {
        ClientDialog.ShowDialog(Alert.AlertType.INFORMATION, "Credits", null, "Program made by:\n- Leandro Adão Fidalgo\n- "
                + "Pedro dos Santos Alves\nFor Distributed Programming");
    }
}
