package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Date;

public class TUser implements Serializable {

    private final int UID;
    private final String UName;
    private final String UUsername;
    private final String UPassword;
    private final String UPhoto;
    private final long UDate;
    private final Date Date;

    public int getUID() {
        return UID;
    }

    public String getUName() {
        return UName;
    }

    public String getUUsername() {
        return UUsername;
    }

    public String getUPassword() {
        return UPassword;
    }

    public String getUPhoto() {
        return UPhoto;
    }

    public long getUDate() {
        return UDate;
    }

    public Date getDate() {
        return Date;
    }

    @Override
    public String toString() {
        return "TUser{" + "UID=" + UID + ", UName=" + UName + ", UUsername=" + UUsername + ", UPassword=" + UPassword + ", UPhoto=" + UPhoto + ", UDate=" + UDate + ", Date=" + Date + '}';
    }

    public TUser(int UID, String UName, String UUsername, String UPassword, String UPhoto, long UDate) {
        this.UID = UID;
        this.UName = UName;
        this.UUsername = UUsername;
        this.UPassword = UPassword;
        this.UPhoto = UPhoto;
        this.UDate = UDate;
        this.Date = new Date(this.UDate);
    }
}
