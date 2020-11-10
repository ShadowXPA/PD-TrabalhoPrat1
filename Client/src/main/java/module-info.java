module pt.isec.deis.lei.pd.trabprat.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens pt.isec.deis.lei.pd.trabprat.client to javafx.fxml;
    exports pt.isec.deis.lei.pd.trabprat.client;
    requires pt.isec.deis.lei.pd.trabprat;
    requires java.base;
}
