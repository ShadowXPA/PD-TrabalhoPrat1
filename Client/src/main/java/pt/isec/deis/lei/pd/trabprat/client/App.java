package pt.isec.deis.lei.pd.trabprat.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.client.config.ClientConfig;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.client.thread.tcp.TCPListener;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public class App extends Application {

    private static Scene scene;
    public static ClientConfig CL_CFG;

    @Override
    public void start(Stage stage) throws IOException {
        CL_CFG.Stage = stage;
        scene = new Scene(loadFXML("Login"), DefaultWindowSizes.DEFAULT_LOGIN_WIDTH, DefaultWindowSizes.DEFAULT_LOGIN_HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        CL_CFG = new ClientConfig();
        Command command = new Command();
        DatagramSocket socket;
        System.out.println("Estou aqui");
        // Connect to server
        CL_CFG.server = Initialize.InitializeServerConection(args);
        socket = Initialize.SendPacketUDPToServer(CL_CFG.server);
        command = Initialize.ReceivePacketUDPFromServer(socket);
        if (command != null) {
            CL_CFG.ServerList = (ArrayList<Server>) command.Body;
        } else {
            System.exit(1);
        }
        
        Initialize.ConnectToTCP();
        Thread thread = new Thread(new TCPListener(CL_CFG.socket, CL_CFG.OOS, CL_CFG.OIS));
        thread.setDaemon(true);
        thread.start();
        
        // Do last
        launch();
        
        
    }

    
    
}
