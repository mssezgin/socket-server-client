package prx;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Message {

    private int msgid = 0;
    private String when = null;
    public String from;
    public String to;
    public String body;

    // constructors
    public Message(String f, String t, String b) {

        from = f;
        to = t;
        body = b;
    }

    public Message(String[] cred) {

        msgid = Integer.parseInt(cred[0]);
        when = cred[1];
        from = cred[2];
        to = cred[3];
        body = cred[4];
    }

    public Message(ResultSet rs) throws SQLException {

        msgid = rs.getInt("msgid");
        when = rs.getString("when");
        from = rs.getString("from");
        to = rs.getString("to");
        body = rs.getString("body");
    }

    public String messageResp() {
        return msgid + "|" + when + "|" + from + "|" + to + "|" + body;
    }

    public void printMsg() {

        System.out.println(
                  "Message id: " + msgid +
                "\nWhen:       " + when +
                "\nFrom:       " + from +
                "\nTo:         " + to +
                "\nBody:       " + body
        );
    }
}
