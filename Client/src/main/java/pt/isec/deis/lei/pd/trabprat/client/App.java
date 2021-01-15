package pt.isec.deis.lei.pd.trabprat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.client.config.ClientConfig;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.thread.tcp.TCPListener;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public class App extends Application {

    private static Scene scene;
    public static ClientConfig CL_CFG;

    @Override
    public void start(Stage stage) throws IOException {
        CL_CFG.Stage = stage;
        scene = new Scene(loadFXML("Login"), DefaultWindowSizes.DEFAULT_LOGIN_WIDTH, DefaultWindowSizes.DEFAULT_LOGIN_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Slackovski");
        stage.setResizable(false);
        stage.setOnCloseRequest(e->{
            e.consume();
            try {
                this.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        CL_CFG.closing = true;
        CL_CFG.getSocket().close();
        super.stop(); //To change body of generated methods, choose Tools | Templates.
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
        connectionToServer(args);
        try {
            launch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void connectionToServer(String[] args) {
        Command command;
        DatagramSocket socket;
        if (args != null) {
            try {
                //Connect to server with UDP
                CL_CFG.server = Initialize.InitializeServerConection(args);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
        socket = Initialize.SendPacketUDPToServer(CL_CFG.server);
        command = Initialize.ReceivePacketUDPFromServer(socket);
        if (command != null) {
            synchronized (CL_CFG.LockSL) {
                CL_CFG.ServerList = (ArrayList<Server>) command.Body;
            }
        } else {
            System.exit(1);
        }
        Initialize.ConnectToTCP();
//        if (args == null) {
//            try {
//                ServerController.Login(CL_CFG.MyUser);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
        try {
            //Thread for TCP listener
            Thread thread = new Thread(new TCPListener(CL_CFG.getSocket(), CL_CFG.getOOS(), CL_CFG.getOIS()));
            thread.setDaemon(true);
            thread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
