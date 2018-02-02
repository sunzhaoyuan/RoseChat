package edu.rosehulman.sunz1.rosechat.SQLService;

import android.os.AsyncTask;
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

//    private String url = "jdbc:sqlserver://${dbserver};databaseName=${dbname};user=${user};password={${pass}}";
    private String url = "jdbc:jtds:sqlserver://${dbserver};databaseName=${dbname};user=${user};password=${pass};";

    private static DatabaseConnectionService Instance;

    private Connection connection = null;

    private String mServerName;
    private String mDatabaseName;
    private String mUsername;
    private String mUserpass;
//
//    private static class DatabaseConnectionServiceHolder {
//        private static final DatabaseConnectionService INSTANCE = new DatabaseConnectionService();
//    }


    private DatabaseConnectionService () {
        this.mServerName = Constants.DB_ServerName;
        this.mDatabaseName = Constants.DB_DatabaseName;
        this.mUsername = Constants.DB_ServerUsername;
        this.mUserpass = Constants.DB_ServerPassword;
//        Log.d(debugTag, this.connection.toString());
    }

    public static synchronized DatabaseConnectionService getInstance() {
        if (Instance == null) {
            Instance = new DatabaseConnectionService();
        }
        return Instance;
    }

    /**
     * This method should connect to sql server through
     * the "application account"
     *
     * @return
     */
    public void connect() {
        Log.d(debugTag, "Entered connect()");

        url = url.replace("${user}", this.mUsername)
                .replace("${pass}", this.mUserpass).
                replace("${dbserver}", this.mServerName)
                .replace("${dbname}", this.mDatabaseName);
//        try { // connected
//            Log.d(debugTag, "Connection Successful 1");
//            connection = DriverManager.getConnection(url);
//            Log.d(debugTag, "Connection Successful 2");
//            return true;
//        } catch (SQLException e) { // not connected
//            Log.d(debugTag, "Connect to sql db failed\n" + e.toString());
//            return false;
//        }

        new ConnectionTask().execute(url);
    }

    public Connection getConnection () { return this.connection; }

    /**
     * This method closes the sql connection.
     *
     * @return true if connection is already closed or is successfully closed
     *          false if failed to close connection
     */
    public boolean closeConnection() {
        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            Log.d(debugTag, "When trying to close SQL Connnection");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private class ConnectionTask extends AsyncTask<String, Integer, Long> {

        protected Long doInBackground(String... params) {

            String url = params[0];

            try {
                connection = DriverManager.getConnection(url);
                Log.d(debugTag, "Connection Successful");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

//        protected void onProgressUpdate(Integer... progress) {
//            setP
//        }
//
//        protected void onPostExecute(Long result) {
//
//        }
    }

}
