package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Date;

public class TChannel implements Serializable {

    private final int CID;
    private final TUser CUID;
    private final String CName;
    private final String CPassword;
    private final long CDate;
    private final Date Date;

    public int getCID() {
        return CID;
    }

    public TUser getCUID() {
        return CUID;
    }

    public String getCName() {
        return CName;
    }

    public String getCPassword() {
        return CPassword;
    }

    public long getCDate() {
        return CDate;
    }

    public Date getDate() {
        return Date;
    }

    @Override
    public String toString() {
        return "TChannel{" + "CID=" + CID + ", CUID=" + CUID.getUID() + ", CName=" + CName + ", CPassword=" + CPassword + ", CDate=" + CDate + ", Date=" + Date + '}';
    }

    public TChannel(int CID, TUser CUID, String CName, String CPassword, long CDate) {
        this.CID = CID;
        this.CUID = CUID;
        this.CName = CName;
        this.CPassword = CPassword;
        this.CDate = CDate;
        this.Date = new Date(this.CDate);
    }
}
