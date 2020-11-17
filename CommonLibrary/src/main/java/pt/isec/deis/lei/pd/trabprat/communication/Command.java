package pt.isec.deis.lei.pd.trabprat.communication;

import java.io.Serializable;

public class Command implements Serializable {

    public int CMD;
    public Object Body;

    @Override
    public String toString() {
        return "Command{" + "CMD=" + CMD + ", Body=" + Body + '}';
    }

    public Command() {
        this(ECommand.CMD_IGNORE);
    }

    public Command(int CMD) {
        this(CMD, null);
    }

    public Command(int CMD, Object Body) {
        this.CMD = CMD;
        this.Body = Body;
    }
}
