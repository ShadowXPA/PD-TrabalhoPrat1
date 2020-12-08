package pt.isec.deis.lei.pd.trabprat.thread.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;

public final class UDPHelper {

//    private static final ByteArrayOutputStream baOS = new ByteArrayOutputStream();
//    private static ObjectOutputStream oOS;
//    private static ByteArrayInputStream baIS;
//    private static ObjectInputStream oIS;
//    private static final ByteArrayOutputStream baOS2 = new ByteArrayOutputStream();
//    private static ObjectOutputStream oOS2;
//    private static ByteArrayInputStream baIS2;
//    private static ObjectInputStream oIS2;
//
//    static {
//        try {
//            oOS = new ObjectOutputStream(baOS);
//            oOS2 = new ObjectOutputStream(baOS2);
//        } catch (Exception ex) {
//            ExceptionHandler.ShowException(ex);
//        }
//    }
    public static Command ReadUDPCommand(DatagramPacket ReceivedPacket) throws IOException, ClassNotFoundException {
        ObjectInputStream oI = new ObjectInputStream(new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength()));
        return (Command) oI.readObject();
//        baIS = new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength());
//        oIS = new ObjectInputStream(baIS);
//        return (Command) oIS.readUnshared();
    }

    public static void SendUDPCommand(DatagramSocket Socket, InetAddress Address, int Port, Command cmd) throws IOException {
        ByteArrayOutputStream bA = new ByteArrayOutputStream();
        ObjectOutputStream oO = new ObjectOutputStream(bA);
        oO.writeUnshared(cmd);
        oO.flush();
        byte[] buffer = bA.toByteArray();
        DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
        Socket.send(SendPacket);
//        synchronized (oOS) {
//            oOS.writeUnshared(cmd);
//            oOS.flush();
//            byte[] buffer = baOS.toByteArray();
//            DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
//            Socket.send(SendPacket);
//        }
    }

    public static Command ReadMulticastCommand(DatagramPacket ReceivedPacket) throws IOException, ClassNotFoundException {
        return ReadUDPCommand(ReceivedPacket);
//        ByteArrayInputStream baIS2 = new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength());
//        ObjectInputStream oIS2 = new ObjectInputStream(baIS2);
//        return (Command) oIS2.readUnshared();
    }

    public static void SendMulticastCommand(MulticastSocket Socket, InetAddress Address, int Port, Command cmd) throws IOException {
//        SendUDPCommand(Socket, Address, Port, cmd);
        ByteArrayOutputStream baOS3 = new ByteArrayOutputStream();
        ObjectOutputStream oOS3 = new ObjectOutputStream(baOS3);
        oOS3.writeUnshared(cmd);
        oOS3.flush();
        byte[] buffer = baOS3.toByteArray();
        DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
        Socket.send(SendPacket);
//        synchronized (oOS2) {
//            oOS2.writeUnshared(cmd);
//            oOS2.flush();
//            byte[] buffer = baOS2.toByteArray();
//            DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
//            Socket.send(SendPacket);
//        }
    }

    private UDPHelper() {
    }
}
