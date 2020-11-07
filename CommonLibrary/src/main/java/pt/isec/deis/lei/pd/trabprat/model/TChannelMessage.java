package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;

public class TChannelMessage implements Serializable {

    private final TChannel CID;
    private final TMessage MID;

    public TChannel getCID() {
        return CID;
    }

    public TMessage getMID() {
        return MID;
    }

    @Override
    public String toString() {
        return "TChannelMessage{" + "CID=" + CID.getCID() + ", MID=" + MID.getMID() + '}';
    }

    public TChannelMessage(TChannel CID, TMessage MID) {
        this.CID = CID;
        this.MID = MID;
    }
}
