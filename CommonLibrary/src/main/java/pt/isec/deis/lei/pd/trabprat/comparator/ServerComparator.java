package pt.isec.deis.lei.pd.trabprat.comparator;

import java.util.Comparator;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public class ServerComparator implements Comparator<Server> {

    @Override
    public int compare(Server o1, Server o2) {
        if (o1 == null || o2 == null)
            return -1;

        int userCount1 = o1.getUserCount();
        int userCount2 = o2.getUserCount();

        return Integer.compare(userCount1, userCount2);
    }
    
}
