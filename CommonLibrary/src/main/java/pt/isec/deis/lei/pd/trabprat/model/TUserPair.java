package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;

public class TUserPair implements Serializable {

    public final TUser User1;
    public final TUser User2;

    public TUserPair(TUser User1, TUser User2) {
        this.User1 = User1;
        this.User2 = User2;
    }
}
