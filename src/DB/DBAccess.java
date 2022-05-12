package DB;



import java.sql.*;

/**
 * DBAccess - this class is for setting up a database to store player credentials in so players can login and register to the game
 * it has functions that use SQL to Interact with the database for finding players and adding players
 */

public class DBAccess {

    private static final String url = "jdbc:postgresql://db2.cmbaezw9qx2t.eu-west-2.rds.amazonaws.com/game";
    private static final String user = "postgres";
    private static final String password = "pg1jlm1X."; //redacted

    private static final String createTable = "CREATE TABLE If NOT EXISTS Users (ID SERIAL PRIMARY KEY, Username varchar(50) NOT NULL, Password varchar(200) NOT NULL);" ;
    private static final String lookupUser = "SELECT * FROM Users WHERE Username = ?";
    private static final String addUser = "INSERT INTO Users (username, password) VALUES (?, ?)";
    private Connection connection;

    public DBAccess() throws SQLException {
        connection = connect();
        createTables();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void createTables() throws SQLException {
        PreparedStatement s = connection.prepareStatement(createTable);
        s.execute();
    }

    public boolean isUserExisting(String username) throws SQLException {
        PreparedStatement s3 = connection.prepareStatement(lookupUser);
        s3.setString(1 , username);
        ResultSet  UsernameResults = s3.executeQuery();
        while (UsernameResults.next()){
            return true;
        }
        return false;
    }

    public boolean validatePassword(String username, String password) throws SQLException {

        PreparedStatement s = connection.prepareStatement(lookupUser);
        s.setString(1 , username);
        ResultSet  UsernameResults = s.executeQuery();
        while (UsernameResults.next()){
          String PasswordFromDB =  UsernameResults.getString("password");
          if (password.equals(PasswordFromDB)) {
              return true;
            }
          else {
              return false;
          }
        }
        return false;
    }


    public void addUser(String username, String password) throws SQLException {

        PreparedStatement s = connection.prepareStatement(addUser);
        s.setString(1 , username);
        s.setString( 2 , password);
        s.execute();
    }
}