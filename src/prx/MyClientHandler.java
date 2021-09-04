package prx;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Arrays;

public class MyClientHandler extends Thread {

    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private MyDB db = null;
    private User client = null;

    public MyClientHandler(Socket s, DataInputStream i, DataOutputStream o, MyDB db) {
        this.socket = s;
        this.inputStream = i;
        this.outputStream = o;
        this.db = db;
        System.out.println("> Client handler started.");
    }

    public String respondLogin(String[] tokens) throws SQLException {

        String resp;
        client = db.authorizeUser(tokens[1], tokens[2]);
        if (client == null) {
            resp = "ERROR|LOGIN_ERROR|Invalid username or password.";
        } else {
            resp = "SUCCESSFUL|LOGGED_IN|" + String.join("|", client.getCredentials());
        }
        return resp;
    }

    public String respondLogout() {
        // TODO: improve this
        return "SUCCESSFUL|LOGGED_OUT";
    }

    public String respondSendMsg(String[] tokens) throws SQLException {

        String resp;
        Message newMsg = new Message(tokens[1], tokens[2], tokens[3]);
        User to = db.showUser(tokens[2]);
        int msgID = db.sendMessage(client, to, newMsg);
        if (msgID == 0) {
            resp = "ERROR|COMMAND_FAILED|Could not send message.";
        } else {
            resp = "SUCCESSFUL|MSG_SENT|" + msgID;
        }
        return resp;
    }

    public String respondShowMsg(String[] tokens) throws SQLException, IOException {
        /* String resp = null;
        Message msg = db.showMessage(Integer.parseInt(tokens[2]));

        if (msg != null && !msg.from.equals(client.getUsername()) && !msg.to.equals(client.getUsername())) {
            int auth = client.authorize(inputStream, outputStream, db);
            if (auth == 0) {
                resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
                return resp;
            } else if (auth == -1) {
                resp = "ERROR|LOGIN_ERROR|Invalid username or password.";
                return resp;
            }
        } */
        String resp;
        if (!db.inboxSentContainsMessage(tokens[1], Integer.parseInt(tokens[2]))) {
            int auth = client.authorize(inputStream, outputStream, db);
            if (auth == 0) {
                resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
                return resp;
            } else if (auth == -1) {
                resp = "ERROR|LOGIN_ERROR|Invalid username or password.";
                return resp;
            }
        }

        Message msg = db.showMessage(Integer.parseInt(tokens[2]));
        if (msg == null) {
            resp = "ERROR|NOT_FOUND|Message not found.";
        } else {
            resp = "SUCCESSFUL|MSG_INFO|" + msg.messageResp();
        }
        return resp;
    }

    public String respondShowInbox(String[] tokens) throws SQLException {

        String resp;
        String inboxIds = db.showInboxSent(tokens[1], "inbox");
        if (inboxIds == null) {
            resp = "ERROR|NOT_FOUND|User not found.";
        } else {
            resp = "SUCCESSFUL|INBOX_INFO|" + inboxIds;
        }
        return resp;
    }

    public String respondShowSent(String[] tokens) throws SQLException {

        String resp;
        String sentIds = db.showInboxSent(tokens[1], "sent");
        if (sentIds == null) {
            resp = "ERROR|NOT_FOUND|User not found.";
        } else {
            resp = "SUCCESSFUL|SENT_INFO|" + sentIds;
        }
        return resp;
    }

    public String respondCreateUser(String[] tokens) throws SQLException, IOException {

        String resp;
        int auth = client.authorize(inputStream, outputStream, db);
        if (auth == 0) {
            resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
            return resp;
        } else if (auth == -1) {
            resp = "ERROR|NOT_FOUND|Invalid username or password.";
            return resp;
        }

        User newUser = new User(Arrays.copyOfRange(tokens, 1, 12));
        int id = db.createUser(newUser);
        if (id == 0) {
            resp = "ERROR|COMMAND_FAILED|Could not create the user. Username or email already exist, or invalid input.";
        } else {
            newUser = db.showUser(id);
            resp = "SUCCESSFUL|USER_INFO|" + String.join("|", newUser.getCredentials());
        }
        return resp;
    }

    public String respondDeleteUser(String[] tokens) throws SQLException, IOException {

        String resp;
        int auth = client.authorize(inputStream, outputStream, db);
        if (auth == 0) {
            resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
            return resp;
        } else if (auth == -1) {
            resp = "ERROR|NOT_FOUND|Invalid username or password.";
            return resp;
        }

        User oldUser = db.showUser(tokens[1]);
        if (oldUser != null && db.deleteUser(oldUser)) {
            resp = "SUCCESSFUL|USER_DELETED|" + oldUser.getUserID();
        } else {
            resp = "ERROR|NOT_FOUND|User not found.";
        }
        return resp;
    }

    public String respondShowUser(String[] tokens) throws SQLException, IOException {

        String resp;
        int auth = client.authorize(inputStream, outputStream, db);
        if (auth == 0) {
            resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
            return resp;
        } else if (auth == -1) {
            resp = "ERROR|NOT_FOUND|Invalid username or password.";
            return resp;
        }

        User user = db.showUser(tokens[1]);
        if (user == null) {
            resp = "ERROR|NOT_FOUND|User not found.";
        } else {
            resp = "SUCCESSFUL|USER_INFO|" + String.join("|", user.getCredentials());
        }
        return resp;
    }

    public String respondUpdateUser(String[] tokens) throws SQLException, IOException {

        String resp;
        int auth = client.authorize(inputStream, outputStream, db);
        if (auth == 0) {
            resp = "ERROR|NO_AUTHORIZATION|You need to be an admin.";
            return resp;
        } else if (auth == -1) {
            resp = "ERROR|NOT_FOUND|Invalid username or password.";
            return resp;
        }

        User userUp = db.showUser(tokens[1]);
        if (userUp == null) {
            resp = "ERROR|NOT_FOUND|User not found.";
            return resp;
        }

        User extension = new User(Arrays.copyOfRange(tokens, 2, 13));
        User userUpdated = db.updateUser(userUp, extension);
        if (userUpdated == null) {
            resp = "ERROR|COMMAND_FAILED|Could not update the user. Username or email already exist, or invalid input.";
        } else {
            userUp = userUpdated;
            resp = "SUCCESSFUL|USER_INFO|" + String.join("|", userUp.getCredentials());
        }
        return resp;
    }

    // run
    public void run() {

        String req;
        String resp;
        String[] tokens;

        while (true) {

            req = "-";
            resp = "-";

            try {

                // get request from the client
                req = inputStream.readUTF();
                tokens = req.split("\\|", -1);

                // serve request
                resp = switch (tokens[0]) {
                    case "LOGIN"       -> this.respondLogin(tokens);
                    case "LOGOUT"      -> this.respondLogout();
                    case "SEND_MSG"    -> this.respondSendMsg(tokens);
                    case "SHOW_MSG"    -> this.respondShowMsg(tokens);
                    case "SHOW_INBOX"  -> this.respondShowInbox(tokens);
                    case "SHOW_SENT"   -> this.respondShowSent(tokens);
                    case "CREATE_USER" -> this.respondCreateUser(tokens);
                    case "DELETE_USER" -> this.respondDeleteUser(tokens);
                    case "SHOW_USER"   -> this.respondShowUser(tokens);
                    case "UPDATE_USER" -> this.respondUpdateUser(tokens);
                    default            -> "ERROR|INVALID_COMMAND|Invalid command.";
                };

                // send respond to the client
                outputStream.writeUTF(resp);

                // logout
                // TODO: delete this dummy logout
                if (tokens[0].equals("LOGOUT") && resp.contains("SUCCESSFUL") && resp.contains("LOGGED_OUT")) {
                    System.out.println("< Client logged out. Client handler terminated.");
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    break;
                }

            } catch (SQLException sqlex) {

                sqlex.printStackTrace();
                System.out.println("SQLException");
                try {
                    outputStream.writeUTF("ERROR|SQLEXCEPTION|SQLException caught.");
                } catch (IOException ioex) {
                    // outputStream.writeUTF("ERROR|EXCEPTION|IOException");
                    System.out.println("ERROR|SQLEXCEPTION|SQLException caught.");
                }

            } catch (IOException ioex) {

                // e.printStackTrace();
                try {
                    System.out.println("< WARNING :: IOException. Client handler terminated.");
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("< IOException socket not closed.");
                    return;
                }
            }
        }
    }
}
