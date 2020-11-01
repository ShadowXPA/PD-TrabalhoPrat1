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
import java.net.SocketException;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.client.config.ClientConfig;
import pt.isec.deis.lei.pd.trabprat.client.config.DefaultWindowSizes;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.communication.UDPHelper;
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
        CL_CFG.server = InitializeServerConection(args);
        socket = SendPacketUDPToServer(CL_CFG.server);
        command = ReceivePacketUDPFromServer(socket);
        if (command != null) {
            CL_CFG.ServerList = (ArrayList<Server>) command.Body;
        } else {
            System.exit(1);
        }
        
        ConnectToTCP();
        LoginUser();
        // Do last
        launch();
    }

    public static Server InitializeServerConection(String[] args) {
        Server server;
        int port_server;
        InetAddress ip_server;
        try {
            ip_server = InetAddress.getByName(args[0]);
            port_server = Integer.parseInt(args[1]);
            System.out.println("Passei aqui!");
        } catch (Exception ex) {
            System.out.println("The port has been with errors: \n" + ex.getMessage());
            return null;
        }
        server = new Server(ip_server, port_server, 0, 0);
        return server;
    }

    public static DatagramSocket SendPacketUDPToServer(Server server) {
        Command command = new Command();
        command.CMD = ECommand.CMD_CONNECT;

        try {
            DatagramSocket socket = new DatagramSocket();
            //UDPHelper.SendUDPCommand(socket, server.getAddress(), server.getUDPPort(), command);
            ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
            oOS.writeObject(command);
            
            byte[] buff = bAOS.toByteArray();
            DatagramPacket packet = new DatagramPacket(buff, buff.length, server.getAddress(), server.getUDPPort());

            socket.send(packet);System.out.println("Passei aqui!");
            return socket;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Command ReceivePacketUDPFromServer(DatagramSocket socket) {
        byte[] buff;
        Command command = new Command();
        ArrayList<Server> aux;

        try {
            //DatagramSocket socket = new DatagramSocket();
            
            DatagramPacket packet = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
            socket.setSoTimeout(1000);
            socket.receive(packet);
            //command = UDPHelper.ReadUDPCommand(packet);
            buff = packet.getData();
            ByteArrayInputStream bAIS = new ByteArrayInputStream(buff);
            //new ObjectOutputStream(new ByteArrayOutputStream());
            ObjectInputStream oIS = new ObjectInputStream(bAIS);

            command = (Command) oIS.readObject();
            System.out.println("Comando: " + command.CMD);
            switch (command.CMD) {
                case ECommand.CMD_ACCEPTED:
                    socket.close();
                    return command;
                case ECommand.CMD_MOVED_PERMANENTLY:
                    Server server;
                    aux = (ArrayList<Server>) command.Body;
                    if(aux == null){
                        System.out.println("Não existem mais servidores disponiveis para se conectar!\n");
                        System.exit(1);
                    }
                    server = aux.get(0);
                    socket.close();
                    socket = SendPacketUDPToServer(server);
                    return ReceivePacketUDPFromServer(socket);
                default:
                    socket.close();
                    return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        socket.close();
        return null;
    }

    public static void ConnectToTCP() {

        try {
            Socket socket = new Socket(CL_CFG.server.getAddress(), CL_CFG.server.getTCPPort());
            ObjectOutputStream oOS = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream oIS = new ObjectInputStream(socket.getInputStream());

            //oOS.writeObject();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void LoginUser(){
        
    }
    
}
