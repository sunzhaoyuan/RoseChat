package edu.rosehulman.sunz1.rosechat.DatabaseService;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 *
 * This class handles the database connection after login successfully.
 * This class applies the Singleton pattern because we want to ensure
 * there is only one DataBaseConnectionService ever.
 * Created by sun on 1/24/18.
 */

public class DatabaseConnectionService {

    private final static String debugTag = Constants.TAG_LOGIN;

    private String url = "jdbc:sqlserver://${dbserver};databaseName=${dbname};user=${user};password={${pass}}";

    private Connection connection = null;

    private String mServerName;
    private String mDatabaseName;

    private static class DatabaseConnectionServiceHolder {
        private static final DatabaseConnectionService INSTANCE = new DatabaseConnectionService();
    }

    private DatabaseConnectionService () {
        this.mServerName = Constants.DB_ServerName;
        this.mDatabaseName = Constants.DB_DatabaseName;
    }

    public static DatabaseConnectionService getInstance() {
        return DatabaseConnectionServiceHolder.INSTANCE;
    }

    /**
     * This method should connect to sql server and
     * add itself to the SharedPrefUtil
     *
     * @param user
     * @param pass
     * @return
     */
    public boolean connect(String user, String pass) {
        url = url.replace("${user}", user)
                .replace("${pass}", pass).
                replace("${dbserver}", this.mServerName)
                .replace("${dbname}", this.mDatabaseName);
        try { // connected
            connection = DriverManager.getConnection(url);
            return true;
        } catch (SQLException e) { // not connected
            Log.d(debugTag, e.toString());
            return false;
        }
    }


    public Connection getConnection () { return this.connection; }

}
