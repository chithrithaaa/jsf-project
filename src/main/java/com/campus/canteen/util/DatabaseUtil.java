package com.campus.canteen.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try {
            Properties prop = new Properties();
            InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (in != null) {
                prop.load(in);
                driver = prop.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = prop.getProperty("db.url", "jdbc:mysql://localhost:3306/campus_canteen_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                user = prop.getProperty("db.user", "root");
                password = prop.getProperty("db.password", "root");
            } else {
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/campus_canteen_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                user = "root";
                password = "root";
            }
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
