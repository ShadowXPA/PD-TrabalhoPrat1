package pt.isec.deis.lei.pd.trabprat.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public int insertUser(TUser user) {
        return db.Insert("TUser",
                new ArrayList<>(List.of("0",
                        "'" + user.getUName() + "'",
                        "'" + user.getUUsername() + "'",
                        "'" + user.getUPassword() + "'",
                        "'" + user.getUPhoto() + "'",
                        "" + new Date().getTime())));
    }

    public DatabaseWrapper(Database db) {
        this.db = db;
    }
}
