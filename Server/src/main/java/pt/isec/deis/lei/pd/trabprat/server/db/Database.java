package pt.isec.deis.lei.pd.trabprat.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String ConnectionString;
    private final String Username;
    private final String Password;
    private Connection con = null;

    private boolean Connect() {
        try {
            con = DriverManager.getConnection(this.ConnectionString, this.Username, this.Password);
            return true;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private void Disconnect() {
        try {
            con.close();
            con = null;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public final int Insert(String Table, String[] Values) {
        StringBuilder Query = new StringBuilder();
        Query.append("INSERT INTO ").append(Table).append(" VALUES(");
        if (Values != null) {
            for (int i = 0; i < Values.length; i++) {
                String val = Values[i];
                if (val == null) {
                    return 0;
                }
                Query.append(val);
                if (i != Values.length - 1) {
                    Query.append(",");
                }
            }
        }
        Query.append(")");
        return this.Insert(Query.toString());
    }

    public final int Insert(String Insert) {
        return BaseDDL(Insert);
    }

    public final int Update(String Update) {
        return BaseDDL(Update);
    }

    public final int Delete(String Delete) {
        return BaseDDL(Delete);
    }

    private final int BaseDDL(String Query) {
        int i = 0;
        if (this.Connect()) {
            try {
                con.setAutoCommit(false);
                try ( Statement sta = con.createStatement()) {
                    i = sta.executeUpdate(Query);
                }
                con.commit();
                this.Disconnect();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return i;
    }

    public Database(String ConnectionString, String Username, String Password) throws SQLException {
        this.ConnectionString = ConnectionString;
        this.Username = Username;
        this.Password = Password;
    }
}
