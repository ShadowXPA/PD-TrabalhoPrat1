package pt.isec.deis.lei.pd.trabprat.client.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.validation.Validator;

public final class ClientDialog {

    private ClientDialog() {
    }

    public static void ShowDialog(AlertType Alert_Type, String Title, String Header, String Description) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert_Type);
            alert.setTitle(Title);
            alert.setHeaderText(Header);
            alert.setContentText(Description);
            alert.showAndWait();
            synchronized (App.CL_CFG.Stage) {
                App.CL_CFG.Stage.notifyAll();
            }
        });
    }

    public static boolean ShowDialog2(TChannel tchannel) throws Exception {
        //Serve quando clicar num canal ao qual nao pertenca ou nao seja owner
        if (tchannel.getCUID().equals(App.CL_CFG.MyUser) || tchannel.getCPassword() == null) {
            return true;
        }
        synchronized (App.CL_CFG.LockCU) {
            for (int i = 0; i < App.CL_CFG.ChannelUsers.size(); i++) {
                if (App.CL_CFG.ChannelUsers.get(i).getCID().equals(tchannel)
                        && App.CL_CFG.ChannelUsers.get(i).getUID().equals(App.CL_CFG.MyUser)) {
                    return true;
                }
            }
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Password of channel");
        dialog.setContentText("Please enter the password of channel:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return AES.Encrypt(result.get()).equals(tchannel.getCPassword());
        }
        return false;
    }

    public static TChannel ShowDialog3(boolean bool) throws Exception {
        //serve para criar e editar os canais
        String str;
        Dialog<TChannel> dialog = new Dialog<>();
        if (bool) {
            str = "Create";
        } else {
            str = "Edit";
        }
        dialog.setTitle(str + " Channel Dialog");
        dialog.setHeaderText(str + " channel");
        ButtonType BtnCreateType = new ButtonType(str, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(BtnCreateType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField ChannelName = new TextField();
        ChannelName.setPromptText("Channel name");
        ChannelName.setDisable(!bool);
        TextField ChannelDescription = new TextField();
        ChannelDescription.setPromptText("Channel description");
        PasswordField ChannelPassword = new PasswordField();
        ChannelPassword.setPromptText("Channel password");
        grid.add(new Label("Channel name:"), 0, 0);
        grid.add(ChannelName, 1, 0);
        grid.add(new Label("Channel description:"), 0, 1);
        grid.add(ChannelDescription, 1, 1);
        grid.add(new Label("Channel password:"), 0, 2);
        grid.add(ChannelPassword, 1, 2);
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> ChannelName.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == BtnCreateType) {
                String pass = null, desc = null;
                if (!ChannelPassword.getText().isEmpty()) {
                    pass = ChannelPassword.getText();
                    if (!Validator.Password(pass)) {
                        ShowDialog(AlertType.ERROR, "Password Error", "Password Error", "The password need a upper case letter, lower case letter and a number!");
                        return null;
                    }
                    try {
                        pass = AES.Encrypt(pass);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (!ChannelDescription.getText().isEmpty()) {
                    desc = ChannelDescription.getText();
                }
                return new TChannel(0, null, ChannelName.getText(), desc, pass, 0);
            }
            return null;
        });
        Optional<TChannel> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static boolean ShowDialog4() {
        //quando um canal está a ser eliminado aparece o seguinte dialog
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Channel Dialog");
        alert.setContentText("Are you sure that want to delete the channel?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            return true;
        } else {
            // ... user chose CANCEL or closed the dialog
            return false;
        }
    }

    public static Pair<String, String> ShowDialog5() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Send Message Dialog");
        dialog.setHeaderText("Write the new message");
        ButtonType BtnCreateType = new ButtonType("Send Message", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(BtnCreateType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField Message_To = new TextField();
        Message_To.setPromptText("Message to");
        TextField Message_Text = new TextField();
        Message_Text.setPromptText("Message text");

        grid.add(new Label("Message to:"), 0, 0);
        grid.add(Message_To, 1, 0);
        grid.add(new Label("Message text:"), 0, 1);
        grid.add(Message_Text, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> Message_To.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == BtnCreateType) {
                if (Message_To.getText().isEmpty() || Message_Text.getText().isEmpty()) {
                    return null;
                } else {
                    return new Pair<>(Message_To.getText(), Message_Text.getText());
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static Pair<String, String> ShowDialog6() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Send File Dialog");
        dialog.setHeaderText("Choose the file to send");
        ButtonType BtnCreateType = new ButtonType("Send File", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(BtnCreateType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField Message_To = new TextField();
        Message_To.setPromptText("Message to");
        final TextField File_path = new TextField();
        File_path.setDisable(true);
        Button btn = new Button();
        btn.setText("Browse...");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ChooseFIle((Button) t.getSource());
            }

            private void ChooseFIle(Button button) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select the file");
                File file = fileChooser.showOpenDialog(App.CL_CFG.Stage);
                if (file == null) {
                    return;
                }
                File_path.setText(file.getAbsolutePath());
            }
        });
        grid.add(new Label("Message to:"), 0, 0);
        grid.add(Message_To, 1, 0);
        grid.add(new Label("File to send:"), 0, 1);
        grid.add(File_path, 1, 1);
        grid.add(btn, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> Message_To.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == BtnCreateType) {
                if (Message_To.getText().isEmpty() || File_path.getText().isEmpty()) {
                    return null;
                } else {
                    return new Pair<>(Message_To.getText(), File_path.getText());
                }
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static String ShowDialog7() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search User");
        dialog.setContentText("Enter a name or username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static void ShowDialog8(ArrayList<TUser> users) {
        Platform.runLater(() -> {
            double db_height = 350;
            double db_width = 250;
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Found users");
            ScrollPane sp = new ScrollPane();
            VBox vb = new VBox();
            for (int i = 0; i < users.size(); i++) {
                Label lb_name = new Label(users.get(i).getUName());
                lb_name.setMinWidth(db_width);
                lb_name.setMaxWidth(db_width);
                Label lb_username = new Label(users.get(i).getUUsername());
                lb_username.setMinWidth(db_width);
                lb_username.setMaxWidth(db_width);
                Separator hs = new Separator(Orientation.HORIZONTAL);
                vb.getChildren().addAll(lb_name, lb_username, hs);
            }
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setContent(vb);
            Scene sc = new Scene(sp);
            st.setMinHeight(db_height);
            st.setMinWidth(db_width);
            st.setMaxHeight(db_height);
            st.setMaxWidth(db_width);
            st.setResizable(false);
            st.setScene(sc);
            st.showAndWait();
        });
    }
}
