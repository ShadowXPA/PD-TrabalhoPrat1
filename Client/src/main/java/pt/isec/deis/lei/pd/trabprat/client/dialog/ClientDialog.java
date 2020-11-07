package pt.isec.deis.lei.pd.trabprat.client.dialog;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
}
