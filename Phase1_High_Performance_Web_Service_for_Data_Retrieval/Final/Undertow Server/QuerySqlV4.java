import java.sql.*;

/**
 * Created by jialingliu on 3/15/16.
 */
public class QuerySqlV4 {
    
    public static String query(String info) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DataSource.getInstance().getConnection();
            stmt = conn.createStatement();
            String sql = String.format("SELECT SQL_CACHE allText FROM queryInfo WHERE info = \'%s\' LIMIT 1", info);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String result = rs.getString("allText");
                result = result.replace("\\n", "\n").
                                replace("\\t", "\t").
                                replace("\\\"", "\"").
                                replace("\\r", "\r").
                                replace("!%^?dawn!%^?", ";") + ";;";
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "\n";
    }
}