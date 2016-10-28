import java.sql.*;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * Created by jialingliu on 3/15/16.
 */
public class QuerySqlV4 {
//    private static String encode(String s) throws IOException{
//        String asB64 = Base64.getUrlEncoder().encodeToString(s.getBytes("utf-8"));
//        return asB64;
//    }
//
//    private static String decode(String s) throws IOException{
//        byte[] asBytes = Base64.getUrlDecoder().decode(s);
//        return new String(asBytes, "utf-8");
//    }
//
//    public static void delete4(long id) {
//        String sql = "DELETE FROM q4 WHERE tweetid = ? ";
//
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        try {
//            conn = DataSource.getInstance().getConnection();
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setLong(1, id);
//            pstmt.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (pstmt != null) {
//                try {
//                    pstmt.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static String query4(long id, short seq, String op, String fields, String payload) {
        // set operation
        if (op.equals("set")) {
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = DataSource.getInstance().getConnection();
                stmt = conn.createStatement();
                // fields number needed to be inserted
                // pay attention to beginning and ending empty string
                String[] fields_arr = fields.split(",", -1);
                String[] payload_arr = payload.split(",", -1);
                int n = fields_arr.length;
                int n1 = payload_arr.length;
                if (n != n1) {
                    return "unsuccess";
                }

                StringBuilder sb = new StringBuilder("INSERT INTO q4 (tweetid, ");
                for (int i = 0 ; i < n ; i++) {
                    String field;
                    if ("text".equals(fields_arr[i]) || "timestamp".equals(fields_arr[i])) {
                        field = "the" + fields_arr[i];
                    } else {
                        field = fields_arr[i];
                    }
                    if (i < n-1) {
                        sb.append(field).append(", ");
                    } else {
                        sb.append(field).append(") VALUES (");
                    }
                }
                sb.append("\"").append(id).append("\"").append(", ");
                for (int i = 0; i < n; i++) {
                    if (i < n-1) {
                        sb.append("\"").append(payload_arr[i]).append("\"").append(", ");
                    } else {
                        sb.append("\"").append(payload_arr[i]).append("\"").append(") ON DUPLICATE KEY UPDATE ");
                    }
                }
                for (int i = 0; i < n; i++) {
                    String field;
                    if ("text".equals(fields_arr[i]) || "timestamp".equals(fields_arr[i])) {
                        field = "the" + fields_arr[i];
                    } else {
                        field = fields_arr[i];
                    }
                    if (i < n-1) {
                        sb.append(field).append("=").append("\"").append(payload_arr[i]).append("\"").append(", ");
                    } else {
                        sb.append(field).append("=").append("\"").append(payload_arr[i]).append("\"");
                    }
                }
                String sql = sb.toString();
                stmt.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
        }
        // get operation
        else {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = DataSource.getInstance().getConnection();
                stmt = conn.createStatement();
                String field;
                if ("text".equals(fields) || "timestamp".equals(fields)) {
                    field = "the" + fields;
                } else {
                    field = fields;
                }
                String sql = String.format("SELECT SQL_CACHE %s FROM q4 WHERE tweetid = \'%s\' LIMIT 1", field, id);
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String result = rs.getString(field);
                    if (result != null) {
                        //return encode(result) + ";";
                        result = result.replaceAll("-", "+").replaceAll("_", "/");
                        return result + ";";

                    }
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
        }
        return "\n";
    }

    private static String getFinalWords(String words, String targets) {
        if ("\n".equals(words)) {
            return targets.replace(",", ":0") + ":0;";
        }
        String[] targetsArr = targets.split(",");
        HashMap<String, Integer> map = new HashMap<>();
        for(String word : targetsArr) {
            map.put(word, 0);
        }
        String[] wordsArr = words.split(",");
        for (String wordcount : wordsArr) {
            String w = wordcount.split(":")[0];
            if (map.containsKey(w)) {
                int c = Integer.parseInt(wordcount.split(":")[1]);
                map.put(w, map.get(w) + c);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(String word : targetsArr) {
            sb.append(word).append(":").append(map.get(word)).append("\n");
        }
        return sb.toString();
    }

    public static String query(String start_date, String end_date, String start_userid, String end_userid, String words) {
        String targets = words;
        words = words.replace(",",":|") + ":";
        Pattern pattern = Pattern.compile(words);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DataSource.getInstance().getConnection();
            stmt = conn.createStatement();
            String sql = String.format("SELECT words FROM rangequery WHERE userid >= %s AND userid <= %s AND ymddate >= %s AND ymddate <= %s", start_userid, end_userid, start_date, end_date);
            rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String result = rs.getString("words");
                if (pattern.matcher(result).find()) {
                    sb.append(result).append(",");
                }
            }
            String result = sb.toString();
            return getFinalWords(result, targets);
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