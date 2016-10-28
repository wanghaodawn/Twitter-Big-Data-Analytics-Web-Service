package mysql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Created by jialingliu on 3/13/16.
 */
public class QuerySql_new {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;

    public static String query(String user_id, String hashtag) {
        String want = "";
        String sql;
        String info = user_id + "_" + hashtag;
        sql = String.format("SELECT all Text AS want FROM queryInfo WHERE info = \'%s\'", info);
        try {
            ConnectionPool pool = ConnectionPool.getInstance();
            conn = pool.getConnection();
            stmt = conn.createStatement();
            System.out.println("creating a statement...");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                want = rs.getString("want");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return want.replace("!%^?dawn!%^?", ";") + ";;";
    }
}
