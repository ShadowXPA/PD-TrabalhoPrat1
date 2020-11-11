package pt.isec.deis.lei.pd.trabprat.server.db;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public final class DatabaseWrapper {

    private final Database db;

    public TUser getUserByID(int UID) {
        return getUserBy("uid", "" + UID);
    }

    public TUser getUserByName(String UName) {
        return getUserBy("uname", "'" + UName + "'");
    }

    public TUser getUserByUsername(String UUsername) {
        return getUserBy("uusername", "'" + UUsername + "'");
    }

    private TUser getUserBy(String by, String what) {
        var info = db.Select("select * from tuser where " + by + "=" + what + "");
        if (info == null || info.isEmpty()) {
            return null;
        }
        return parseUser(info.get(0));
    }

    public ArrayList<TUser> getAllUsers() {
        var info = db.Select("select * from tuser");
        if (info == null || info.isEmpty()) {
            return null;
        }
        ArrayList<TUser> Users = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            Users.add(parseUser(info.get(i)));
        }
        return Users;
    }

    private TUser parseUser(HashMap<String, String> Set) {
        int uid = Integer.parseInt(Set.get("UID"));
        String uname = Set.get("UName");
        String uusername = Set.get("UUsername");
        String upassword = Set.get("UPassword");
        String uphoto = Set.get("UPhoto");
        long udate = Long.parseLong(Set.get("UDate"));
        return new TUser(uid, uname, uusername, upassword, uphoto, udate);
    }

    public TMessage getMessageByID(int MID) {
        return getMessageBy("mid", "" + MID);
    }

    private TMessage getMessageBy(String by, String what) {
        var info = db.Select("select * from tmessage where " + by + "=" + what + "");
        if (info == null || info.isEmpty()) {
            return null;
        }
        return parseMessage(info.get(0));
    }

    public ArrayList<TMessage> getAllMessages() {
        var info = db.Select("select * from tmessages");
        if (info == null || info.isEmpty()) {
            return null;
        }
        ArrayList<TMessage> Messages = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            Messages.add(parseMessage(info.get(i)));
        }
        return Messages;
    }

    private TMessage parseMessage(HashMap<String, String> Set) {
        int mid = Integer.parseInt(Set.get("MID"));
        TUser muid = getUserByID(Integer.parseInt(Set.get("MUID")));
        String mtext = Set.get("MText");
        String mpath = Set.get("MPath");
        long mdate = Long.parseLong(Set.get("MDate"));
        return new TMessage(mid, muid, mtext, mpath, mdate);
    }

    public TChannel getChannelByID(int CID) {
        return getChannelBy("cid", "" + CID);
    }

    public TChannel getChannelByName(String CName) {
        return getChannelBy("cname", "'" + CName + "'");
    }

    private TChannel getChannelBy(String by, String what) {
        var info = db.Select("select * from tchannel where " + by + "=" + what + "");
        if (info == null || info.isEmpty()) {
            return null;
        }
        return parseChannel(info.get(0));
    }

    public ArrayList<TChannel> getAllChannels() {
        var info = db.Select("select * from tchannel");
        if (info == null || info.isEmpty()) {
            return null;
        }
        ArrayList<TChannel> Channels = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            Channels.add(parseChannel(info.get(i)));
        }
        return Channels;
    }

    private TChannel parseChannel(HashMap<String, String> Set) {
        int cid = Integer.parseInt(Set.get("CID"));
        TUser cuid = getUserByID(Integer.parseInt(Set.get("CUID")));
        String cname = Set.get("CName");
        String cdesc = Set.get("CDescription");
        String cpassword = Set.get("CPassword");
        long cdate = Long.parseLong(Set.get("CDate"));
        return new TChannel(cid, cuid, cname, cdesc, cpassword, cdate);
    }

    public ArrayList<TChannelUser> getAllChannelUsers() {
        var info = db.Select("select * from tchannelusers");
        ArrayList<TChannelUser> channelUsers = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            channelUsers.add(parseChannelUser(info.get(i)));
        }
        return channelUsers;
    }

    public ArrayList<TChannelUser> getAllUsersFromChannelID(int CID) {
        var info = db.Select("select u.CID, u.UID from tchannelusers u"
                + " where u.CID = " + CID);
        ArrayList<TChannelUser> channelUsers = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            channelUsers.add(parseChannelUser(info.get(i)));
        }
        return channelUsers;
    }

    public ArrayList<TChannelUser> getAllUsersFromChannelName(String CName) {
        var info = db.Select("select u.CID, u.UID from tchannelusers u,"
                + " tchannel c where c.CID = u.CID and c.CName = '"
                + CName.replace("'", "''") + "'");
        ArrayList<TChannelUser> channelUsers = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            channelUsers.add(parseChannelUser(info.get(i)));
        }
        return channelUsers;
    }

    private TChannelUser parseChannelUser(HashMap<String, String> Set) {
        TChannel CID = getChannelByID(Integer.parseInt(Set.get("CID")));
        TUser UID = getUserByID(Integer.parseInt(Set.get("UID")));
        return new TChannelUser(CID, UID);
    }

    public boolean doesUserBelongToChannel(TChannel Channel, TUser User) {
        var info = db.Select("select cu.CID, cu.UID from tchannelusers cu"
                + " where cu.CID = " + Channel.getCID()
                + " and cu.UID = " + User.getUID());
        if (info == null) {
            return false;
        }
        return !info.isEmpty();
    }

    public ArrayList<TChannelMessage> getAllMessagesFromChannelID(int CID) {
        var info = db.Select("select * from tchannelmessages where CID = " + CID);
        ArrayList<TChannelMessage> messages = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            messages.add(parseChannelMessage(info.get(i)));
        }
        return messages;
    }

    private TChannelMessage parseChannelMessage(HashMap<String, String> Set) {
        TChannel CID = getChannelByID(Integer.parseInt(Set.get("CID")));
        TMessage MID = getMessageByID(Integer.parseInt(Set.get("MID")));
        return new TChannelMessage(CID, MID);
    }

    public ArrayList<TDirectMessage> getAllDMByUserID(int UID) {
        var info = db.Select("select d.MID, d.UID"
                + " from tdirectmessage d,"
                + " tmessage m where d.MID = m.MID and (d.UID = "
                + UID + " or m.MUID = " + UID + ")");
        if (info == null || info.isEmpty()) {
            return null;
        }
        ArrayList<TDirectMessage> DMs = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            DMs.add(parseDirectMessage(info.get(i)));
        }
        return DMs;
    }

    public ArrayList<TUser> getOtherUserFromDM(ArrayList<TDirectMessage> DMs, TUser user) {
        ArrayList<TUser> users = new ArrayList<>();
        for (int i = 0; i < DMs.size(); i++) {
            if (DMs.get(i).getUID().equals(user)) {
                TUser u = DMs.get(i).getMID().getMUID();
                if (!users.contains(u)) {
                    users.add(u);
                }
            } else {
                TUser u = DMs.get(i).getUID();
                if (!users.contains(u)) {
                    users.add(u);
                }
            }
        }
        return users;
    }

    private TDirectMessage parseDirectMessage(HashMap<String, String> Set) {
        TMessage mid = getMessageByID(Integer.parseInt(Set.get("MID")));
        TUser receiver = getUserByID(Integer.parseInt(Set.get("UID")));
        return new TDirectMessage(mid, receiver);
    }

    public int insertUser(TUser User) {
        String uphoto = "null";
        if (User.getUPhoto() != null) {
            uphoto = "'" + User.getUPhoto().replace("'", "''") + "'";
        }
        return db.Insert("TUser",
                new ArrayList<>(List.of("0",
                        "'" + User.getUName().replace("'", "''") + "'",
                        "'" + User.getUUsername().replace("'", "''") + "'",
                        "'" + User.getUPassword().replace("'", "''") + "'",
                        uphoto,
                        "" + new Date().getTime())));
    }

    public int insertMessage(TMessage Message) {
        String mpath = "null";
        if (Message.getMPath() != null) {
            mpath = "'" + Message.getMPath().replace("'", "''") + "'";
        }
        return db.Insert("TMessage",
                new ArrayList<>(List.of("0",
                        "" + Message.getMUID().getUID(),
                        "'" + Message.getMText().replace("'", "''") + "'",
                        mpath,
                        "" + new Date().getTime())));
    }

    public int insertChannel(TChannel Channel) {
        String cdesc = "null", cpass = "null";
        if (Channel.getCDescription() != null) {
            cdesc = "'" + Channel.getCDescription().replace("'", "''") + "'";
        }
        if (Channel.getCPassword() != null) {
            cpass = "'" + Channel.getCPassword().replace("'", "''") + "'";
        }
        return db.Insert("TChannel",
                new ArrayList<>(List.of("0",
                        "" + Channel.getCUID().getUID(),
                        "'" + Channel.getCName().replace("'", "''") + "'",
                        cdesc,
                        cpass,
                        "" + new Date().getTime())));
    }

    public int insertChannelUser(TChannel Channel, TUser User) {
        return db.Insert("TChannelUsers",
                new ArrayList<>(List.of("" + Channel.getCID(),
                        "" + User.getUID())));
    }

    public DatabaseWrapper(Database db) {
        this.db = db;
    }
}
