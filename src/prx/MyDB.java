package prx;

import java.sql.*;

public class MyDB {

    private String url = "jdbc:postgresql://localhost:5432/myolddb";
    private String user = "postgres";
    private String password = "safak";
    private Connection con = null;

    public void connectDB() throws SQLException {

        con = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to database.");

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Users LIMIT 1;");
            rs = st.executeQuery("SELECT * FROM Messages LIMIT 1;");
        } catch (SQLException e) {
            Statement st = con.createStatement();
            String query = "CREATE TABLE Users (" +
                    "userid serial NOT NULL, " +
                    "admin bit NOT NULL, " +
                    "username text NOT NULL, " +
                    "password text NOT NULL, " +
                    "email text, " +
                    "name text, " +
                    "lastname text, " +
                    "gender \"char\", " +
                    "dateofbirth date," +
                    "inbox integer[] DEFAULT '{}', " +
                    "sent integer[] DEFAULT '{}'" +
            ");";
            st.executeUpdate(query);
            query = "ALTER TABLE Users ADD CONSTRAINT primary_userid PRIMARY KEY (userid);";
            st.executeUpdate(query);
            query = "ALTER TABLE Users ADD CONSTRAINT unique_username UNIQUE (username);";
            st.executeUpdate(query);
            query = "ALTER TABLE Users ADD CONSTRAINT unique_email UNIQUE (email);";
            st.executeUpdate(query);
            query = "INSERT INTO Users (admin, username, password) VALUES (1::bit, 'superuser', '0000');";
            st.executeUpdate(query);
            query = "CREATE TABLE Messages (" +
                    "msgid serial NOT NULL, " +
                    "\"when\" timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP(0), " +
                    "\"from\" text NOT NULL, " +
                    "\"to\" text NOT NULL, " +
                    "body text" +
            ");";
            st.executeUpdate(query);
            query = "ALTER TABLE Messages ADD CONSTRAINT primary_msgid PRIMARY KEY (msgid);";
            st.executeUpdate(query);
            st.close();
            System.out.println("Users and Messages tables do not exist. Created new ones.");
        }
    }

    public void closeDB() throws SQLException {
        if (con != null)
            con.close();
    }

    public User authorizeUser(String username, String password) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE username='" + username + "';");
        if (rs.next() && password.equals(rs.getString("password"))) {
            return new User(rs);
        }
        return null;
    }

    public int createUser(User user) throws SQLException {

        String[] cred = user.getCredentials();
        String query = "INSERT INTO Users VALUES (" +
                    "DEFAULT, " +
                    cred[1] + "::bit, " +
                    "'" + cred[2] + "', " +
                    "'" + cred[3] + "', " +
                    (cred[4].isBlank() ? "DEFAULT, " : "'" + cred[4] + "', ") +
                    (cred[5].isBlank() ? "DEFAULT, " : "'" + cred[5] + "', ") +
                    (cred[6].isBlank() ? "DEFAULT, " : "'" + cred[6] + "', ") +
                    (cred[7].isBlank() ? "DEFAULT, " : "'" + cred[7] + "', ") +
                    (cred[8].isBlank() ? "DEFAULT, " : "'" + cred[8] + "', ") +
                    "DEFAULT, " +
                    "DEFAULT" +
                ") RETURNING userid;";

        Statement st = con.createStatement();
        try {
            ResultSet rs = st.executeQuery(query);
            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public boolean deleteUser(User user) throws SQLException {

        Statement st = con.createStatement();
        try {
            st.executeUpdate("DELETE FROM Users WHERE userid=" + user.getUserID() +
                    " AND username='" + user.getUsername() + "';");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public User showUser(int userid) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE userid=" + userid + ";");
        if (rs.next()) {
            return new User(rs);
        } else {
            return null;
        }
    }

    public User showUser(String username) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE username='" + username + "';");
        if (rs.next()) {
            return new User(rs);
        } else {
            return null;
        }
    }

    public User updateUser(User user, User extension) throws SQLException {

        String[] cred = extension.getCredentials();
        String columns = (cred[1].isBlank() ? "" : "admin="        + cred[1] + "::bit|") +
                (cred[2].isBlank() ? "" : "username='"    + cred[2] + "'|") +
                (cred[3].isBlank() ? "" : "password='"    + cred[3] + "'|") +
                (cred[4].isBlank() ? "" : "email='"       + cred[4] + "'|") +
                (cred[5].isBlank() ? "" : "name='"        + cred[5] + "'|") +
                (cred[6].isBlank() ? "" : "lastname='"    + cred[6] + "'|") +
                (cred[7].isBlank() ? "" : "gender='"      + cred[7] + "'|") +
                (cred[8].isBlank() ? "" : "dateofbirth='" + cred[8] + "'|");
        columns = String.join(", ", columns.split("\\|"));
        String query = "UPDATE Users SET " + columns +
                " WHERE userid=" + user.getUserID() + " AND username='" + user.getUsername() + "';";

        Statement st = con.createStatement();
        try {
            st.executeUpdate(query);
            return showUser(user.getUserID());
        } catch (SQLException e) {
            return null;
        }
    }

    public int sendMessage(User from, User to, Message msg) throws SQLException {

        if (to == null) return 0;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("INSERT INTO Messages (\"from\", \"to\", body) VALUES ('" +
                from.getUsername() + "', '" + to.getUsername() + "', '" + msg.body + "') RETURNING msgid;"
        );

        if (!rs.next()) return 0;

        int msgID = rs.getInt(1);
        st.executeUpdate("UPDATE Users SET sent=ARRAY_APPEND(sent, " + msgID + ") " +
                "WHERE username='" + from.getUsername() + "';"
        );
        st.executeUpdate("UPDATE Users SET inbox=ARRAY_APPEND(inbox, " + msgID + ") " +
                "WHERE username='" + to.getUsername() + "';"
        );

        return msgID;
    }

    public Message showMessage(int msgid) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Messages WHERE msgid=" + msgid + ";");
        if (rs.next()) {
            return new Message(rs);
        } else {
            return null;
        }
    }

    public boolean inboxSentContainsMessage(String username, int msgid) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM users WHERE username='" + username + "' " +
                "AND (inbox @> ARRAY[" + msgid + "] OR sent @> ARRAY[" + msgid + "]);");
        return rs.next();
    }

    public String showInboxSent(String username, String is) throws SQLException {

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT " + is + " FROM Users WHERE username='" + username + "';");
        if (rs.next()) {
            return rs.getString(is);
        } else {
            return null;
        }
    }
}
