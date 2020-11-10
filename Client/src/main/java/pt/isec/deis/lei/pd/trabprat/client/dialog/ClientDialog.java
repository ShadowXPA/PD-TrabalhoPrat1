package pt.isec.deis.lei.pd.trabprat.client.dialog;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;

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
}
