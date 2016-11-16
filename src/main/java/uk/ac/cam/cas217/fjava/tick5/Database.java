package uk.ac.cam.cas217.fjava.tick5;

import uk.ac.cam.cl.fjava.messages.RelayMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 12/11/16.
 */
public class Database {
    private final Connection connection;
    private final boolean verbose;

    private static final String DUPLICATE_TABLE_CODE = "42504";

    public Database(String databasePath) throws SQLException {
        this(databasePath, false);
    }

    private Database(String databasePath, boolean verbose) throws SQLException {
        connection = DriverManager.getConnection(
            String.format("jdbc:hsqldb:file:%s", databasePath),
            "SA",
            ""
        );
        this.verbose = verbose;

        try (Statement delayStmt = connection.createStatement()) {
            //Always update data on disk
            delayStmt.execute("SET WRITE_DELAY FALSE");
        }

        connection.setAutoCommit(false);

        createMessagesTable();
        createStatisticsTable();
    }

    private void createMessagesTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE messages(nick VARCHAR(255) NOT NULL," +
                "message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL);"
            );
            connection.commit();
        } catch (SQLException e) {
            if (!e.getSQLState().equals(DUPLICATE_TABLE_CODE)) {
                throw e;
            }

            if (verbose) {
                System.out.println("Warning: Database table \"messages\" already exists.");
            }
        }
    }

    private void createStatisticsTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE statistics(key VARCHAR(255),value INT);"
            );
            connection.commit();
        } catch (SQLException e) {
            if (!e.getSQLState().equals(DUPLICATE_TABLE_CODE)) {
                throw e;
            }

            // This table has already been inserted, we don't want to continue
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "INSERT INTO statistics(key,value) VALUES ('Total messages',0);" +
                "INSERT INTO statistics(key,value) VALUES ('Total logins',0);"
            );

            connection.commit();
        }
    }

    public void incrementLogins() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("UPDATE statistics SET value = value+1 WHERE key='Total logins';");
            connection.commit();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void addMessage(RelayMessage message) {
        String statementTemplate = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(statementTemplate)) {
            statement.setString(1, message.getFrom());
            statement.setString(2, message.getMessage());
            statement.setLong(3, message.getCreationTime().getTime());

            statement.executeUpdate();

            connection.commit();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public List<RelayMessage> getRecent() {
        String statementTemplate =
            "SELECT nick,message,timeposted FROM messages " +
            "ORDER BY timeposted DESC LIMIT 10";

        List<RelayMessage> outputList = new LinkedList<>();

        try (PreparedStatement recentMessages = connection.prepareStatement(statementTemplate);
             ResultSet rs = recentMessages.executeQuery()) {
            while (rs.next()) {
                outputList.add(
                    0,
                    new RelayMessage(rs.getString(1), rs.getString(2), new Date(rs.getLong(3)))
                );
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        return Collections.unmodifiableList(outputList);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java uk.ac.cam.cas217.fjava.tick5.Database <database name>");
            return;
        }
        Database database = new Database(args[0], true);

        database.addMessage(
            new RelayMessage("Alastair", "Hello, Andy", new Date(System.currentTimeMillis()))
        );

        database.getRecent().stream().forEachOrdered(relayMessage -> System.out.println(String.format(
            "%s: %s [%d]",
            relayMessage.getFrom(),
            relayMessage.getMessage(),
            relayMessage.getCreationTime().getTime()
        )));
    }
}
