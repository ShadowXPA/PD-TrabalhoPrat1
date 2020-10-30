package pt.isec.deis.lei.pd.trabprat.server.thread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;

public class UDPListener implements Runnable {

    @Override
    public void run() {
        while (true) {
            try ( DatagramSocket ServerSocket = new DatagramSocket(DefaultConfig.DEFAULT_UDP_PORT)) {
                DatagramPacket ReceivedPacket = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
                DatagramPacket SendPacket;
                ByteArrayOutputStream baOS = new ByteArrayOutputStream();
                ObjectOutputStream oOS = new ObjectOutputStream(baOS);
                ByteArrayInputStream baIS;
                ObjectInputStream oIS;

                while (true) {
                    Command cmd;
                    // receive user connection atempt
                    ReceivedPacket.setLength(DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
                    ServerSocket.receive(ReceivedPacket);
                    baIS = new ByteArrayInputStream(ReceivedPacket.getData(), 0, ReceivedPacket.getLength());
                    oIS = new ObjectInputStream(baIS);
                    cmd = (Command) oIS.readUnshared();

                    if (cmd.CMD == ECommand.CMD_CONNECT) {
                        // check other servers (lock)
                        // send user accepted or not
                        cmd.CMD = ECommand.CMD_ACCEPTED;
                        cmd.Body = null;
                        oOS.writeUnshared(cmd);
                        oOS.flush();
                        var buffer = baOS.toByteArray();
                        SendPacket = new DatagramPacket(buffer, buffer.length, ReceivedPacket.getAddress(), ReceivedPacket.getPort());
                        ServerSocket.send(SendPacket);
                    } else {
                        // send CMD_FORBIDDEN command
                    }

                }
            } catch (Exception ex) {
                Logger.getLogger(UDPListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
