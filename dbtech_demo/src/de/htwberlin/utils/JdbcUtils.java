package de.htwberlin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

//import org.apache.commons.dbcp.ConnectionFactory;
//import org.apache.commons.dbcp.DriverManagerConnectionFactory;
//import org.apache.commons.dbcp.PoolableConnectionFactory;
//import org.apache.commons.dbcp.PoolingDataSource;
//import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.sun.org.apache.xml.internal.utils.ObjectPool;

/**
 * The class implements auxiliary methods for JDBC applications.
 * Especially checked exceptions are handled.
 * @author ingo claﬂen
 */
public class JdbcUtils {

    /** The constant holds the local logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * The method creates a pooled datasource. Four connections are pooled.
     * @param dbUrl identifies the database.
     * @param uname holds the user name.
     * @param passwd holds the password.
     * @param readOnly indicates a read only datasource-
     * @param autocommit indicates a datasource with autocommit.
     * @return the new pooled data source.
     */
//    public static DataSource getPooledDataSource(String dbUrl, String uname,
//            String passwd, boolean readOnly, boolean autocommit) {
//		ObjectPool connectionPool = new GenericObjectPool(null, 4);
//
//        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
//                dbUrl, uname, passwd);
//
//        new PoolableConnectionFactory(connectionFactory, connectionPool, null,
//                null, readOnly, autocommit);
//
//        return new PoolingDataSource(connectionPool);
//    }

    /**
     * The method loads the driver.
     * @param driver is the driver to load.
     */
    public static void loadDriver(String driver) {
        try {
            Class.forName(driver);
            LOGGER.info("driver class <" + driver + "> loaded");
        } catch (ClassNotFoundException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method gets an connection using the driver manager.
     * @param url is database url.
     * @param user holds the database user.
     * @param passwd holds the user password.
     * @return a connection to the database.
     * @see DriverManager#getConnection(String, String, String)
     */
    public static Connection getConnectionViaDriverManager(String url,
            String user, String passwd) {
        try {
            Connection connection = DriverManager.getConnection(url, user,
                    passwd);
            LOGGER.info("connection got");
            return connection;
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method gets an connection using the data source.
     * @param datasource holds the data source.
     * @return a connection to the database.
     * @see DataSource#getConnection()
     */
    public static Connection getConnectionViaDataSource(DataSource datasource) {
        try {
            Connection connection = datasource.getConnection();
            LOGGER.info("connection got");
            return connection;
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method closes the connection.
     * @param connection is the connection to close.
     * @see Connection#close()
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                LOGGER.info("connection closed");
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method closes the connection and ignores exceptions.
     * @param connection is the connection to close.
     * @see #closeConnection()
     */
    public static void closeConnectionQuietly(Connection connection) {
        try {
            closeConnection(connection);
        } catch (RuntimeException exp) {
            // ignore exception, just log
            LOGGER.error(null,exp);
        }
    }

    /**
     * The method closes the resultSet.
     * @param resultSet is the result set to close.
     * @see ResultSet#close()
     */
    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
                LOGGER.info("result set closed");
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method closes the result set and ignores exceptions.
     * @param resultSet is the result set to close.
     * @see #closeResultSet()
     */
    public static void closeResultSetQuietly(ResultSet resultSet) {
        try {
            closeResultSet(resultSet);
        } catch (RuntimeException exp) {
            // ignore exception, just log
            LOGGER.error(null,exp);
        }
    }

    /**
     * The method closes the statement.
     * @param statement is the statement to close.
     * @see Statement#close()
     */
    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
                LOGGER.info("statement closed");
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * The method closes the statement and ignores exceptions.
     * @param statement is the statement to close.
     * @see #closeStatement()
     */
    public static void closeStatementQuietly(Statement statement) {
        try {
            closeStatement(statement);
        } catch (RuntimeException exp) {
            // ignore exception, just log
            LOGGER.error(null,exp);
        }
    }

    /**
     * The method returns a string representation for a result set.
     * @param resultSet is the result set to convert.
     * @param displayWidths configures the width of the display
     * @return the result set as string representation.
     */
    public static String resultSetToString(ResultSet resultSet,
            int[] displayWidths) {
        try {
            ResultSetMetaData meta = resultSet.getMetaData();
            StringBuffer bar = new StringBuffer();
            StringBuffer buffer = new StringBuffer();
            int cols = meta.getColumnCount();
            int row_count = 0;
            int i, width = 0;

            // Prepare headers for each of the columns
            // The display should look like:
            //  --------------------------------------
            //  | Column One | Column Two |
            //  --------------------------------------
            //  | Row 1 Value | Row 1 Value |
            //  --------------------------------------

            // create the bar that is as long as the total of all columns
            for (i = 1; i <= cols; i++) {
                width += displayWidths[i - 1];
            }
            width += 1 + cols;
            for (i = 0; i < width; i++) {
                bar.append('-');
            }
            bar.append('\n');
            buffer.append(bar.toString() + "|");
            // After the first bar goes the column labels
            for (i = 1; i <= cols; i++) {
                StringBuffer filler = new StringBuffer();
                String label = meta.getColumnLabel(i);
                int size = displayWidths[i - 1];
                int x;

                // If the label is longer than the column is wide,
                // then we truncate the column label
                if (label.length() > size) {
                    label = label.substring(0, size);
                }
                // If the label is shorter than the column, pad it with spaces
                if (label.length() < size) {
                    int j;

                    x = (size - label.length()) / 2;
                    for (j = 0; j < x; j++) {
                        filler.append(' ');
                    }
                    label = filler + label + filler;
                    if (label.length() > size) {
                        label = label.substring(0, size);
                    } else {
                        while (label.length() < size) {
                            label += " ";
                        }
                    }
                }
                // Add the column header to the buffer
                buffer.append(label + "|");
            }
            // Add the lower bar
            buffer.append("\n" + bar.toString());
            // Format each row in the result set and add it on
            while (resultSet.next()) {
                row_count++;

                buffer.append('|');
                // Format each column of the row
                for (i = 1; i <= cols; i++) {
                    StringBuffer filler = new StringBuffer();
                    Object value = resultSet.getObject(i);
                    int size = displayWidths[i - 1];
                    String str;

                    if (resultSet.wasNull()) {
                        str = "NULL";
                    } else {
                        str = value.toString();
                    }
                    if (str.length() > size) {
                        str = str.substring(0, size);
                    }
                    if (str.length() < size) {
                        int j, x;

                        x = (size - str.length()) / 2;
                        for (j = 0; j < x; j++) {
                            filler.append(' ');
                        }
                        str = filler + str + filler;
                        if (str.length() > size) {
                            str = str.substring(0, size);
                        } else {
                            while (str.length() < size) {
                                str += " ";
                            }
                        }
                    }
                    buffer.append(str + "|");
                }
                buffer.append("\n");
            }
            // Stick a row count up at the top
            if (row_count == 0) {
                buffer = new StringBuffer("No rows selected.\n");
            } else if (row_count == 1) {
                buffer = new StringBuffer("1 row selected.\n"
                        + buffer.toString() + bar.toString());
            } else {
                buffer = new StringBuffer(row_count + " rows selected.\n"
                        + buffer.toString() + bar.toString());
            }
            return buffer.toString();
        } catch (SQLException exp) {
            LOGGER
                    .error("Can't convert result set into string representation.");
            return null;
        }
    }

}
