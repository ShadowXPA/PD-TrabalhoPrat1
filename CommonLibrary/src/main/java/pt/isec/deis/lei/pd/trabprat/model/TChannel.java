package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TChannel implements Serializable {

    private final int CID;
    private final TUser CUID;
    private final String CName;
    private final String CDescription;
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

    public String getCDescription() {
        return CDescription;
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
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.CID;
        hash = 97 * hash + Objects.hashCode(this.CUID);
        hash = 97 * hash + Objects.hashCode(this.CName);
        hash = 97 * hash + Objects.hashCode(this.CDescription);
        hash = 97 * hash + Objects.hashCode(this.CPassword);
        hash = 97 * hash + (int) (this.CDate ^ (this.CDate >>> 32));
        hash = 97 * hash + Objects.hashCode(this.Date);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TChannel other = (TChannel) obj;
        return this.CID == other.CID;
    }

    @Override
    public String toString() {
        return "TChannel{" + "CID=" + CID + ", CUID=" + CUID.getUID() + ", CName=" + CName + ", CDescription=" + CDescription + ", CPassword=" + CPassword + ", CDate=" + CDate + ", Date=" + Date + '}';
    }

    public TChannel(int CID, TUser CUID, String CName, String CDescription, String CPassword, long CDate) {
        this.CID = CID;
        this.CUID = CUID;
        this.CName = CName;
        this.CDescription = CDescription;
        this.CPassword = CPassword;
        this.CDate = CDate;
        this.Date = new Date(this.CDate);
    }
}
