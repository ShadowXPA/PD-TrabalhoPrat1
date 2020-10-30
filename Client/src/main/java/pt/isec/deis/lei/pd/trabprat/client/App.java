package pt.isec.deis.lei.pd.trabprat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import pt.isec.deis.lei.pd.trabprat.client.config.ClientConfig;

public class App extends Application {

    private static Scene scene;
    private static ClientConfig CL_CFG;

    @Override
    public void start(Stage stage) throws IOException {
        CL_CFG.Stage = stage;
        scene = new Scene(loadFXML("Login"), 400, 350);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        CL_CFG = new ClientConfig();
        // Connect to server
        
        // Do last
        launch();
    }

}