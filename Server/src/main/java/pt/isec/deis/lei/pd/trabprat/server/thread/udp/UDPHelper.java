package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.communication.Command;

public final class UDPHelper {

    private static final ByteArrayOutputStream baOS = new ByteArrayOutputStream();
    private static ObjectOutputStream oOS;
    private static ByteArrayInputStream baIS;
    private static ObjectInputStream oIS;

    static {
        try {
            oOS = new ObjectOutputStream(baOS);
        } catch (Exception ex) {
            Logger.getLogger(UDPHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Command ReadUDPCommand(DatagramPacket ReceivedPacket) throws IOException, ClassNotFoundException {
        baIS = new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength());
        oIS = new ObjectInputStream(baIS);
        return (Command) oIS.readUnshared();
    }

    public static void SendUDPCommand(DatagramSocket Socket, InetAddress Address, int Port, Command CMD) throws IOException {
        oOS.writeUnshared(CMD);
        oOS.flush();
        byte[] buffer = baOS.toByteArray();
        DatagramPacket SendPacket = new DatagramPacket(buffer, buffer.length, Address, Port);
        Socket.send(SendPacket);
    }

    private UDPHelper() {
    }
}
