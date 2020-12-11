package pt.isec.deis.lei.pd.trabprat.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public class Database {

    private final String ConnectionString;
    private final String Host;
    private final String Port;
    private final String Schema;
    private final String Username;
    private final String Password;
    private Connection con = null;

    public String getSchema() {
        return Schema;
    }

    private boolean Connect() {
        try {
            con = DriverManager.getConnection(this.ConnectionString, this.Username, this.Password);
            return true;
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
            this.Disconnect();
            return false;
        }
    }

    private void Disconnect() {
        try {
            if (!con.isClosed()) {
                con.close();
            }
            con = null;
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public final ArrayList<HashMap<String, String>> Select(String Table, ArrayList<String> Columns, String Where, String OrderBy, String Limit) {
        if (Table == null || Table.isBlank()) {
            return null;
        }

        var Query = new StringBuilder();
        Query.append("SELECT ");
        if (Columns != null) {
            for (int i = 0; i < Columns.size(); i++) {
                var col = Columns.get(i);
                if (col == null || col.isBlank()) {
                    return null;
                }

                Query.append(col);
                if (i != Columns.size() - 1) {
                    Query.append(",");
                }
            }
        } else {
            Query.append("*");
        }

        Query.append(" FROM ").append(Table);
        if (!(Where == null || Where.isBlank())) {
            Query.append(" WHERE ").append(Where);
        }
        if (!(OrderBy == null || OrderBy.isBlank())) {
            Query.append(" ORDER BY ").append(OrderBy);
        }
        if (!(Limit == null || Limit.isBlank())) {
            Query.append(" LIMIT ").append(Limit);
        }

        return this.Select(Query.toString());
    }

    public final ArrayList<HashMap<String, String>> Select(String Select) {
        return this.ExecuteReader(Select);
    }

    public final int Insert(String Table, ArrayList<String> Values) {
        if (Table == null || Table.isBlank()) {
            return -2;
        }

        var Query = new StringBuilder();
        Query.append("INSERT INTO ").append(Table).append(" VALUES(");
        if (Values != null) {
            for (int i = 0; i < Values.size(); i++) {
                String val = Values.get(i);
                if (val == null) {
                    return -3;
                }
                Query.append(val);
                if (i != Values.size() - 1) {
                    Query.append(",");
                }
            }
        }
        Query.append(")");

        return this.Insert(Query.toString());
    }

    public final int Insert(String Insert) {
        return ExecuteNonQuery(Insert);
    }

    public final int Update(String Table, HashMap<String, String> Set, String Where, String OrderBy, String Limit) {
        if (Table == null || Table.isBlank() || Set == null || Set.isEmpty()) {
            return -2;
        }

        var Query = new StringBuilder();
        Query.append("UPDATE ").append(Table).append(" SET ");
        for (String Key : Set.keySet()) {
            String Value = Set.get(Key);
            if (Key == null || Value == null || Key.isBlank() || Value.isBlank()) {
                return -3;
            }
            Query.append(Key).append("=").append(Value).append(",");
        }
        Query.deleteCharAt(Query.lastIndexOf(","));

        if (!(Where == null || Where.isBlank())) {
            Query.append(" WHERE ").append(Where);
        }
        if (!(OrderBy == null || OrderBy.isBlank())) {
            Query.append(" ORDER BY ").append(OrderBy);
        }
        if (!(Limit == null || Limit.isBlank())) {
            Query.append(" LIMIT ").append(Limit);
        }

        return this.Update(Query.toString());
    }

    public final int Update(String Update) {
        return ExecuteNonQuery(Update);
    }

    public final int Delete(String Table, String Where, String OrderBy, String Limit) {
        if (Table == null || Table.isBlank()) {
            return -2;
        }

        var Query = new StringBuilder();
        if (!(Where == null || Where.isBlank())) {
            Query.append(" WHERE ").append(Where);
        }
        if (!(OrderBy == null || OrderBy.isBlank())) {
            Query.append(" ORDER BY ").append(OrderBy);
        }
        if (!(Limit == null || Limit.isBlank())) {
            Query.append(" LIMIT ").append(Limit);
        }

        return this.Delete(Query.toString());
    }

    public final int Delete(String Delete) {
        return ExecuteNonQuery(Delete);
    }

    private ArrayList<HashMap<String, String>> ExecuteReader(String Query) {
        if (this.Connect()) {
            ArrayList<HashMap<String, String>> Result = new ArrayList<>();
            try {
                try ( Statement sta = con.createStatement();  ResultSet RSet = sta.executeQuery(Query)) {
                    ResultSetMetaData RSMD = RSet.getMetaData();
                    int ColumnCount = RSMD.getColumnCount();
                    while (RSet.next()) {
                        HashMap<String, String> temp = new HashMap<>();
                        for (int i = 1; i <= ColumnCount; i++) {
                            String Key = RSMD.getColumnLabel(i);
                            String Value = RSet.getString(i);
                            temp.put(Key, Value);
                        }
                        Result.add(temp);
                    }
                }
                this.Disconnect();
                return Result;
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
        return null;
    }

    private int ExecuteNonQuery(String Query) {
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
                ExceptionHandler.ShowException(ex);
            }
        }
        return i;
    }

    public Database(String Host, String Port, String Schema, String Username, String Password) throws SQLException, ClassNotFoundException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
        this.Host = Host;
        this.Port = Port;
        this.Schema = Schema;
        this.Username = Username;
        this.Password = Password;
        this.ConnectionString = "jdbc:mysql://" + this.Host + ":" + this.Port + "/" + this.Schema
                + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
                + "&useSSL=false&allowPublicKeyRetrieval=true";
    }
}
