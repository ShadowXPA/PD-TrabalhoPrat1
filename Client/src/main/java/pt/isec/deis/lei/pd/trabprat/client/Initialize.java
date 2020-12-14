package pt.isec.deis.lei.pd.trabprat.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import static pt.isec.deis.lei.pd.trabprat.client.App.CL_CFG;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public final class Initialize {

    private Initialize() {
    }

    public static Server InitializeServerConection(String[] args) throws UnknownHostException {
        Server server;
        int port_server;
        InetAddress ip_server;
        try {
            //IP server and port
            ip_server = InetAddress.getByName(args[0]);
            port_server = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            System.out.println("The port has been with errors: \n" + ex.getMessage());
            ip_server = InetAddress.getByName("127.0.0.1");
            port_server = DefaultConfig.DEFAULT_UDP_PORT;
        }
        //create conection and return server
        server = new Server(null, 0, ip_server, port_server, 0, 0);
        return server;
    }

    public static DatagramSocket SendPacketUDPToServer(Server server) {
        Command command = new Command();
        command.CMD = ECommand.CMD_CONNECT;
        try {
            //create socket, byte array output and object output
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
            oOS.writeObject(command);
            byte[] buff = bAOS.toByteArray();
            //Create a datagram packet for connection
            DatagramPacket packet = new DatagramPacket(buff, buff.length, server.getAddress(), server.getUDPPort());
            socket.send(packet);
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
            DatagramPacket packet = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
            socket.setSoTimeout(1000);
            socket.receive(packet);
            buff = packet.getData();
            //bytearray input to receive things from server
            ByteArrayInputStream bAIS = new ByteArrayInputStream(buff);
            ObjectInputStream oIS = new ObjectInputStream(bAIS);
            command = (Command) oIS.readObject();
            System.out.println("Comando: " + command.CMD);
            switch (command.CMD) {
                case ECommand.CMD_ACCEPTED: {
                    Server server;
                    aux = (ArrayList<Server>) command.Body;
                    if (aux == null) {
                        System.out.println("Não existem mais servidores disponiveis para se conectar!\n");
                        System.exit(1);
                    }
                    server = aux.get(0);
                    CL_CFG.server = server;
                    socket.close();
                    return command;
                }
                case ECommand.CMD_MOVED_PERMANENTLY: {
                    Server server;
                    server = (Server) command.Body;
                    if (server == null) {
                        System.out.println("Não existem mais servidores disponiveis para se conectar!\n");
                        System.exit(1);
                    }
                    socket.close();
                    socket = SendPacketUDPToServer(server);
                    return ReceivePacketUDPFromServer(socket);
                }
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
            //create socket TCP to connect with server
            Socket socket = new Socket(CL_CFG.server.getAddress(), CL_CFG.server.getTCPPort());
            CL_CFG.setSocket(socket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
