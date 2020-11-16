package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TUser implements Serializable {

    private final int UID;
    private final String UName;
    private final String UUsername;
    private String UPassword;
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

    public void setPassword() {
        UPassword = null;
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
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.UID;
        hash = 71 * hash + Objects.hashCode(this.UName);
        hash = 71 * hash + Objects.hashCode(this.UUsername);
        hash = 71 * hash + Objects.hashCode(this.UPassword);
        hash = 71 * hash + Objects.hashCode(this.UPhoto);
        hash = 71 * hash + (int) (this.UDate ^ (this.UDate >>> 32));
        hash = 71 * hash + Objects.hashCode(this.Date);
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
        final TUser other = (TUser) obj;
        return this.UID == other.UID;
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
