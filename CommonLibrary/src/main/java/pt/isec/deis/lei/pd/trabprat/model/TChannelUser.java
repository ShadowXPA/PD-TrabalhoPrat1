package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;

public class TChannelUser implements Serializable {

    private final TChannel CID;
    private final TUser UID;

    public TChannel getCID() {
        return CID;
    }

    public TUser getUID() {
        return UID;
    }

    @Override
    public String toString() {
        return "TChannelUser{" + "CID=" + CID.getCID() + ", UID=" + UID.getUID() + '}';
    }

    public TChannelUser(TChannel CID, TUser UID) {
        this.CID = CID;
        this.UID = UID;
    }
}
