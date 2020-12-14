package pt.isec.deis.lei.pd.trabprat.comparator;

import java.util.Comparator;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public class ServerStartComparator implements Comparator<Server> {

    @Override
    public int compare(Server o1, Server o2) {
        if (o1 == null || o2 == null) {
            return -1;
        }

        long svStart1 = o1.ServerStart;
        long svStart2 = o2.ServerStart;

        return Long.compare(svStart1, svStart2);
    }
}
