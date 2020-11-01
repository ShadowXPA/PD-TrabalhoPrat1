package pt.isec.deis.lei.pd.trabprat.client;

import java.io.IOException;
import javafx.fxml.FXML;
import pt.isec.deis.lei.pd.trabprat.client.App;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}