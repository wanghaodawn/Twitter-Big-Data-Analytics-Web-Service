package mysql;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
/**
 * Created by jialingliu on 3/13/16.
 */
public class ConnectionPool {

    private static ConnectionPool instance = new ConnectionPool();
    private HikariDataSource dataSource = null;

    private ConnectionPool(){
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(200);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("port", 3306);
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("user", "root");
        config.addDataSourceProperty("password", "ccelder");
        config.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/tweets");
        dataSource = new HikariDataSource(config);
    }

    public static ConnectionPool getInstance (){
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
