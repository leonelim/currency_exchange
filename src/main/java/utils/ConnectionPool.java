package utils;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import exceptions.DatabaseNotAvailableException;

public class ConnectionPool {
    private static final BasicDataSource dataSource = new BasicDataSource();
    static {
        dataSource.setUrl(PropertiesUtil.get("db.url"));
        dataSource.setDriverClassName(PropertiesUtil.get("db.driver-class-name"));

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }
    public static Connection get() {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException("Could not connect to the database");
        }
    }
    private ConnectionPool() {}
}
