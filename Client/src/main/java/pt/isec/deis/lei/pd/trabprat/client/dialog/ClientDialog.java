package pt.isec.deis.lei.pd.trabprat.client.dialog;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
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
        });
    }

    public static boolean ShowDialog2(TChannel tchannel) throws Exception {
        if (tchannel.getCUID().equals(App.CL_CFG.MyUser) || tchannel.getCPassword() == null) {
            return true;
        }
        for (int i = 0; i < App.CL_CFG.ChannelUsers.size(); i++) {
            if (App.CL_CFG.ChannelUsers.get(i).getCID().equals(tchannel)
                    && App.CL_CFG.ChannelUsers.get(i).getUID().equals(App.CL_CFG.MyUser)) {
                return true;
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
                return new TChannel(0, App.CL_CFG.MyUser, ChannelName.getText(), desc, pass, 0);
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
}
