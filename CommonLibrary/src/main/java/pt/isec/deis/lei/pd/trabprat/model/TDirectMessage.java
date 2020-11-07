package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;

public class TDirectMessage implements Serializable {

    private final TMessage MID;
    private final TUser UID;

    public TMessage getMID() {
        return MID;
    }

    public TUser getUID() {
        return UID;
    }

    @Override
    public String toString() {
        return "TDirectMessage{" + "MID=" + MID.getMID() + ", UID=" + UID.getUID() + '}';
    }

    public TDirectMessage(TMessage MID, TUser UID) {
        this.MID = MID;
        this.UID = UID;
    }
}
