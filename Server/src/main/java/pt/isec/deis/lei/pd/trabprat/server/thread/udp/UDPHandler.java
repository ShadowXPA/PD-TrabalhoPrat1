package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import java.io.File;
import java.io.FileNotFoundException;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.server.model.SyncDBPackage;

public class UDPHandler implements Runnable {

    private final DatagramSocket ServerSocket;
    private final DatagramPacket ReceivedPacket;
    private final String IP;
    private final ServerConfig SV_CFG;

    @Override
    public void run() {
        try {
            // Read command
            Command cmd = UDPHelper.ReadUDPCommand(ReceivedPacket);
            Main.Log("[" + IP + "]", "" + cmd.CMD);

            // React accordingly
            switch (cmd.CMD) {
                case ECommand.CMD_CONNECT: {
                    HandleConnect(IP);
                    break;
                }
                case ECommand.CMD_HELLO: {
                    HandleHello(cmd);
                    break;
                }
                case ECommand.CMD_SYNC: {
                    HandleSync(cmd);
                    break;
                }
                case ECommand.CMD_SYNC_DB: {
                    HandleSyncDB(cmd);
                    break;
                }
                case ECommand.CMD_SYNC_F: {
                    HandleSyncFile(cmd);
                    break;
                }
                default: {
                    // Send CMD_FORBIDDEN command
                    UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), new Command(ECommand.CMD_FORBIDDEN));
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleConnect(String IP) throws IOException {
        // Ask other servers via multicast if they have less than 50% of the load (use lock)
        Command cmd;
        Server Accept = null; // Get response via multicast
        ArrayList<Server> body = new ArrayList<>();
        Server thisSv;
        synchronized (SV_CFG) {
            thisSv = new Server(SV_CFG.ServerID, SV_CFG.ServerStart, SV_CFG.ExternalIP, SV_CFG.UDPPort, SV_CFG.TCPPort, SV_CFG.Clients.size());
            Accept = thisSv;
            body.add(thisSv);//SV_CFG.ClientList.size()));
            body.addAll(SV_CFG.ServerList);
            if (body.size() > 1) {
                body.sort(SV_CFG.SvComp);
                Server lowestCapSv = body.get(0);
                if ((thisSv.getUserCount() * 0.5) >= lowestCapSv.getUserCount()) {
                    Accept = lowestCapSv;
                }
            }
        }
        if (thisSv.equals(Accept)) {
            // Accepted
            cmd = new Command(ECommand.CMD_ACCEPTED, body);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        } else {
            // Rejected
            cmd = new Command(ECommand.CMD_MOVED_PERMANENTLY, Accept);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        }
        Main.Log("[Server] to " + IP, "" + cmd.CMD);
    }

    public UDPHandler(DatagramSocket ServerSocket, DatagramPacket ReceivedPacket, String IP, ServerConfig SV_CFG) throws IOException {
        this.ReceivedPacket = ReceivedPacket;
        this.ServerSocket = ServerSocket;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }

    private void HandleHello(Command cmd) {
        GenericPair<String, Server> gp = (GenericPair<String, Server>) cmd.Body;
        Server sv = gp.value;
        Main.Log("Found server", sv.ServerID);
        synchronized (SV_CFG) {
            if (!SV_CFG.ServerList.contains(sv) && !sv.ServerID.equals(SV_CFG.ServerID)) {
                SV_CFG.ServerList.add(sv);
            }
        }
    }

    private void HandleSync(Command cmd) throws IOException {
        GenericPair<String, Server> gp = (GenericPair<String, Server>) cmd.Body;
        Server sv = gp.value;
        synchronized (SV_CFG) {
            var db = SV_CFG.DB;
            var users = db.getAllUsers();
            var messages = db.getAllMessages();
            var channels = db.getAllChannels();
            var channelUsers = db.getAllChannelUsers();
            var channelMessages = db.getAllChannelMessages();
            var directMessages = db.getAllDirectMessages();
            ArrayList<GenericPair<Integer, Integer>> cU = new ArrayList<>();
            ArrayList<GenericPair<Integer, Integer>> cM = new ArrayList<>();
            ArrayList<GenericPair<Integer, Integer>> dM = new ArrayList<>();
            channelUsers.forEach(c -> cU.add(new GenericPair<>(c.getCID().getCID(), c.getUID().getUID())));
            channelMessages.forEach(c -> cM.add(new GenericPair<>(c.getMID().getMID(), c.getCID().getCID())));
            directMessages.forEach(c -> dM.add(new GenericPair<>(c.getMID().getMID(), c.getUID().getUID())));
            GenericPair<String, SyncDBPackage> syncPack = new GenericPair<>(SV_CFG.ServerID, new SyncDBPackage(users,
                    messages, channels, cU, cM, dM));
            UDPHelper.SendUDPCommand(ServerSocket, sv.getAddress(), sv.getUDPPort(),
                    new Command(ECommand.CMD_SYNC_DB, syncPack));
            // TODO: Send Files
            String BaseDir = SV_CFG.DBConnection.getSchema() + ExplorerController.BASE_DIR;
            File AvatarDir = new File(BaseDir + ExplorerController.AVATAR_SUBDIR);
            File[] AvatarFiles = AvatarDir.listFiles();
            for (var f : AvatarFiles) {
                int offset = 0;
                int length = 0;
                String path = f.getPath();
                String fileName = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
                String extension = path.substring(path.lastIndexOf("."));
                
                FileChunk fc = new FileChunk(null, offset, length, fileName, null, extension);
            }
        }
    }

    private void HandleSyncDB(Command cmd) {
        GenericPair<String, SyncDBPackage> gp = (GenericPair<String, SyncDBPackage>) cmd.Body;
        Server sv = new Server(gp.key);
        SyncDBPackage dbPack = gp.value;
        synchronized (SV_CFG) {
            if (SV_CFG.ServerList.contains(sv)) {
                // Insert into the DB everything needed
                var db = SV_CFG.DB;
                dbPack.Users.forEach(u -> db.devInsertUser(u));
                dbPack.Channels.forEach(c -> db.devInsertChannel(c));
                dbPack.Messages.forEach(m -> db.devInsertMessage(m));
                dbPack.ChannelUsers.forEach(cu -> db.devInsertChannelUser(cu.key, cu.value));
                dbPack.ChannelMessages.forEach(cm -> db.devInsertChannelMessage(cm.value, cm.key));
                dbPack.DirectMessages.forEach(dm -> db.devInsertDirectMessage(dm.value, dm.key));
            } else {
                Main.Log("[Warning]", "Server '" + gp.key + "' not found");
            }
            synchronized (SV_CFG.DB) {
                SV_CFG.DB.notifyAll();
            }
        }
    }

    private void HandleSyncFile(Command cmd) throws IOException, FileNotFoundException, InterruptedException {
        GenericPair<String, FileChunk> gp = (GenericPair<String, FileChunk>) cmd.Body;
        Server sv = new Server(gp.key);
        FileChunk fc = gp.value;
        synchronized (SV_CFG) {
            if (SV_CFG.ServerList.contains(sv)) {
                // Write file
                boolean hasGUID = (fc.getGUID() != null);
                ExplorerController.WriteFile(SV_CFG.DBConnection.getSchema(),
                        !hasGUID ? ExplorerController.AVATAR_SUBDIR : ExplorerController.FILES_SUBDIR,
                        !hasGUID ? fc.getUsername() + fc.getExtension() : fc.getGUID().toString() + fc.getExtension(),
                        fc.getFilePart(),
                        fc.getOffset(),
                        fc.getLength());
            } else {
                Main.Log("[Warning]", "Server '" + gp.key + "' not found");
            }
        }
    }
}
