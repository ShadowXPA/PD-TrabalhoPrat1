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
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public final class UDPHelper {

    private static final ByteArrayOutputStream baOS = new ByteArrayOutputStream();
    private static ObjectOutputStream oOS;
    private static ByteArrayInputStream baIS;
    private static ObjectInputStream oIS;

    static {
        try {
            oOS = new ObjectOutputStream(baOS);
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public static Command ReadUDPCommand(DatagramPacket ReceivedPacket) throws IOException, ClassNotFoundException {
        baIS = new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength());
        oIS = new ObjectInputStream(baIS);
        return (Command) oIS.readUnshared();
    }

    public static void SendUDPCommand(DatagramSocket Socket, InetAddress Address, int Port, Command cmd) throws IOException {
        synchronized (oOS) {
            oOS.writeUnshared(cmd);
            oOS.flush();
            byte[] buffer = baOS.toByteArray();
            DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
            Socket.send(SendPacket);
        }
    }

    public static void SendMulticastCommand(MulticastSocket Socket, InetAddress Address, int Port, Command cmd) throws IOException {
        synchronized (oOS) {
            oOS.writeUnshared(cmd);
            oOS.flush();
            byte[] buffer = baOS.toByteArray();
            DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
            Socket.send(SendPacket);
        }
    }

    private UDPHelper() {
    }
}
