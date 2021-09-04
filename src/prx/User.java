package prx;

import java.io.*;
import java.sql.*;

public class User {

    // userid, admin, username, password, email, name, lastname, gender, dateofbirth, inbox, sent
    private String[] credentials = null;

    // constructors
    public User() {}

    // TODO: use deep copy but might be redundant
    public User(String[] c) {
        this.credentials = c;
    }

    public User(ResultSet rs) throws SQLException {

        this.credentials = new String[11];
        this.credentials[0] = rs.getString("userid");
        this.credentials[1] = rs.getString("admin");
        this.credentials[2] = rs.getString("username");
        this.credentials[3] = rs.getString("password");
        this.credentials[4] = rs.getString("email");
        this.credentials[5] = rs.getString("name");
        this.credentials[6] = rs.getString("lastname");
        this.credentials[7] = rs.getString("gender");
        this.credentials[8] = rs.getString("dateofbirth");
        this.credentials[9] = rs.getString("inbox");
        this.credentials[10] = rs.getString("sent");
    }

    public User(int id, boolean a, String un, String pw, String e, String n, String ln, String g, String dob, String i, String s) {

        this.credentials = new String[11];
        this.credentials[0] = String.valueOf(id);
        this.credentials[1] = (a ? "1" : "0");
        this.credentials[2] = un;
        this.credentials[3] = pw;
        this.credentials[4] = e;
        this.credentials[5] = n;
        this.credentials[6] = ln;
        this.credentials[7] = g;
        this.credentials[8] = dob;
        this.credentials[9] = i;
        this.credentials[10] = s;
    }

    // setters
    public void setUserID(int userid) {
        this.credentials[0] = String.valueOf(userid);
    }

    public void setAdmin(String admin) {
        this.credentials[1] = admin;
    }

    public void setUsername(String username) {
        this.credentials[2] = username;
    }

    public void setPassword(String password) {
        this.credentials[3] = password;
    }

    public void setEmail(String email) {
        this.credentials[4] = email;
    }

    public void setName(String name) {
        this.credentials[5] = name;
    }

    public void setLastname(String lastname) {
        this.credentials[6] = lastname;
    }

    public void setGender(String gender) {
        this.credentials[7] = gender;
    }

    public void setDateOfBirth(String dateofbirth) {
        this.credentials[8] = dateofbirth;
    }

    public void setInbox(String inbox) {
        this.credentials[9] = inbox;
    }

    public void setSent(String sent) {
        this.credentials[10] = sent;
    }

    // getters
    public String[] getCredentials() {
        return this.credentials;
    }

    public int getUserID() {
        return Integer.parseInt(this.credentials[0]);
    }

    public String getAdmin() {
        return this.credentials[1];
    }

    public String getUsername() {
        return this.credentials[2];
    }

    public String getPassword() {
        return this.credentials[3];
    }

    public String getEmail() {
        return this.credentials[4];
    }

    public String getName() {
        return this.credentials[5];
    }

    public String getLastname() {
        return this.credentials[6];
    }

    public String getGender() {
        return this.credentials[7];
    }

    public String getDateOfBirth() {
        return this.credentials[8];
    }

    public String getInbox() {
        return this.credentials[9];
    }

    public String getSent() {
        return this.credentials[10];
    }

    // methods
    public int authorize(DataInputStream in, DataOutputStream out, MyDB db) throws SQLException, IOException {

        out.writeUTF("WARNING|AUTHORIZATION");
        String req = in.readUTF();
        String[] tokens = req.split("\\|", -1);
        if (tokens[0].equals("AUTHORIZATION")) {
            User user = db.authorizeUser(tokens[1], tokens[2]);
            if (user == null) {
                return -1; // wrong username or password
            } else if (user.getAdmin().equals("1")) {
                return 1; // admin, okay
            } else {
                return 0; // not admin
            }
        }
        return -1;
    }

    public void printUser() {

        System.out.println(
                  "User id:       " + this.credentials[0] +
                "\nAdmin:         " + this.credentials[1] +
                "\nUsername:      " + this.credentials[2] +
                "\nPassword:      " + this.credentials[3] +
                "\nE-mail:        " + this.credentials[4] +
                "\nName:          " + this.credentials[5] +
                "\nLastname:      " + this.credentials[6] +
                "\nGender:        " + this.credentials[7] +
                "\nDate of birth: " + this.credentials[8] +
                "\nInbox:         " + this.credentials[9] +
                "\nSent:          " + this.credentials[10]
        );
    }
}
