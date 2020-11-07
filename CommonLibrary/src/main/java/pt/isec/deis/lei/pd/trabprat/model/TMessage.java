package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Date;

public class TMessage implements Serializable {

    private final int MID;
    private final TUser MUID;
    private final String MText;
    private final String MPath;
    private final long MDate;
    private final Date Date;

    public int getMID() {
        return MID;
    }

    public TUser getMUID() {
        return MUID;
    }

    public String getMText() {
        return MText;
    }

    public String getMPath() {
        return MPath;
    }

    public long getMDate() {
        return MDate;
    }

    public Date getDate() {
        return Date;
    }

    @Override
    public String toString() {
        return "TMessage{" + "MID=" + MID + ", MUID=" + MUID.getUID() + ", MText=" + MText + ", MPath=" + MPath + ", MDate=" + MDate + ", Date=" + Date + '}';
    }

    public TMessage(int MID, TUser MUID, String MText, String MPath, long MDate) {
        this.MID = MID;
        this.MUID = MUID;
        this.MText = MText;
        this.MPath = MPath;
        this.MDate = MDate;
        this.Date = new Date(this.MDate);
    }
}
