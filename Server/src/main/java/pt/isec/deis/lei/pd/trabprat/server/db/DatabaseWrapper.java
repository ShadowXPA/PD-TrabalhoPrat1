package pt.isec.deis.lei.pd.trabprat.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
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

    public int insertUser(TUser User) {
        return db.Insert("TUser",
                new ArrayList<>(List.of("0",
                        "'" + User.getUName() + "'",
                        "'" + User.getUUsername() + "'",
                        "'" + User.getUPassword() + "'",
                        "'" + User.getUPhoto() + "'",
                        "" + new Date().getTime())));
    }

    public int insertMessage(TMessage Message) {
        return db.Insert("TMessage",
                new ArrayList<>(List.of("0",
                        "" + Message.getMUID().getUID(),
                        "'" + Message.getMText() + "'",
                        "'" + Message.getMPath() + "'",
                        "" + new Date().getTime())));
    }

    public int insertChannel(TChannel Channel) {
        return db.Insert("TChannel",
                new ArrayList<>(List.of("0",
                        "" + Channel.getCUID().getUID(),
                        "'" + Channel.getCName() + "'",
                        "'" + Channel.getCDescription() + "'",
                        "'" + Channel.getCPassword() + "'",
                        "" + new Date().getTime())));
    }

    public DatabaseWrapper(Database db) {
        this.db = db;
    }
}
