package prx;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class MyClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static User me = null;

    // request
    public static String requestLogin() throws IOException {

        String req = "LOGIN";
        while (true) {
            System.out.print(">>> Username: ");
            String un = br.readLine();
            if (!un.isBlank()) {
                req = req + "|" + un;
                break;
            }
        }
        while (true) {
            System.out.print(">>> Password: ");
            String pw = br.readLine();
            if (!pw.isBlank()) {
                req = req + "|" + pw;
                break;
            }
        }
        return req;
    }

    public static String requestLogout() {
        return "LOGOUT" + "|" + me.getUsername();
    }

    public static String requestAuthorization() throws IOException {

        System.out.println("WARNING :: Authorization required.");
        String req = "AUTHORIZATION";
        System.out.println(">>> Username: " + me.getUsername());
        req = req + "|" + me.getUsername();
        while (true) {
            System.out.print(">>> Password: ");
            String pw = br.readLine();
            if (!pw.isBlank()) {
                req = req + "|" + pw;
                break;
            }
        }
        return req;
    }

    public static void printHelp() {
        // TODO: prepare and print help page
        System.out.println("Valid commands are\n" +
                "\tLOGOUT, SENDMSG, SHOWMSG, SHOWINBOX, SHOWSENT,\n" +
                "\tCREATEUSER, DELETEUSER, SHOWUSER, UPDATEUSER.");
    }

    public static void printQuit() {
        System.out.println("LOGOUT before quitting.");
    }

    public static String requestSendMsg() throws IOException {

        String req = "SEND_MSG" + "|" + me.getUsername();
        System.out.print(">>> To (username): ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Message body: ");
        req = req + "|" + br.readLine();
        return req;
    }

    public static String requestShowMsg() throws IOException {
        String req = "SHOW_MSG" + "|" + me.getUsername();
        System.out.print(">>> Message id: ");
        req = req + "|" + br.readLine();
        return req;
    }

    public static String requestShowInbox() {
        return "SHOW_INBOX" + "|" + me.getUsername();
    }

    public static String requestShowSent() {
        return "SHOW_SENT" + "|" + me.getUsername();
    }

    public static String requestCreateUser() throws IOException {

        String req = "CREATE_USER" + "|"; // for userid
        System.out.println("Enter credentials for the new user. Press enter to leave empty.\n'*' means required.");

        while (true) {
            System.out.print(">>> Will be an admin? (Y/N) *: ");
            String yn = br.readLine().toUpperCase();
            if (yn.equals("Y")) {
                req = req + "|" + "1";
                break;
            } else if (yn.equals("N")) {
                req = req + "|" + "0";
                break;
            }
        }
        while (true) {
            System.out.print(">>> Username *: ");
            String un = br.readLine();
            if (!un.isBlank()) {
                req = req + "|" + un;
                break;
            }
        }
        while (true) {
            System.out.print(">>> Password *: ");
            String pw = br.readLine();
            if (!pw.isBlank()) {
                req = req + "|" + pw;
                break;
            }
        }
        System.out.print(">>> E-mail address: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Firstname: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Lastname: ");
        req = req + "|" + br.readLine();
        while (true) {
            System.out.print(">>> Gender (M/F): ");
            String mf = br.readLine().toUpperCase();
            if (mf.isBlank() || mf.equals("M") || mf.equals("F")) {
                req = req + "|" + mf;
                break;
            }
        }
        System.out.print(">>> Date of birth (YYYY-MM-DD): ");
        req = req + "|" + br.readLine();
        req = req + "|" + "|"; // for inbox and sent

        return req;
    }

    public static String requestDeleteUser() throws IOException {
        String req = "DELETE_USER";
        System.out.print(">>> Who will be deleted? (username): ");
        req = req + "|" + br.readLine();
        return req;
    }

    public static String requestShowUser() throws IOException {
        String req = "SHOW_USER";
        System.out.print(">>> Who will be shown? (username): ");
        req = req + "|" + br.readLine();
        return req;
    }

    public static String requestUpdateUser() throws IOException {

        String req = "UPDATE_USER";
        System.out.print(">>> Who will be updated? (username): ");

        req = req + "|" + br.readLine() + "|"; // for userid
        System.out.println("Enter new credentials. Press enter to not change.");
        while (true) {
            System.out.print(">>> Will be an admin? (Y/N): ");
            String yn = br.readLine().toUpperCase();
            if (yn.equals("Y")) {
                req = req + "|" + "1";
                break;
            } else if (yn.equals("N")) {
                req = req + "|" + "0";
                break;
            } else if (yn.isBlank()) {
                req = req + "|";
                break;
            }
        }
        System.out.print(">>> Username: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Password: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> E-mail address: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Firstname: ");
        req = req + "|" + br.readLine();
        System.out.print(">>> Lastname: ");
        req = req + "|" + br.readLine();
        while (true) {
            System.out.print(">>> Gender (M/F): ");
            String mf = br.readLine().toUpperCase();
            if (mf.isBlank() || mf.equals("M") || mf.equals("F")) {
                req = req + "|" + mf;
                break;
            }
        }
        System.out.print(">>> Date of birth (YYYY-MM-DD): ");
        req = req + "|" + br.readLine();
        req = req + "|" + "|"; // for inbox and sent

        return req;
    }

    // response
    public static void responseLoggedIn(String[] tokens) {
        me = new User(Arrays.copyOfRange(tokens, 2, 13));
        System.out.println("Login successful. Type HELP to see commands.");
    }

    public static void responseLoggedOut() {
        System.out.println("Logging out.");
    }

    public static void responseMsgSent(String[] tokens) {
        System.out.println("Message with id " + tokens[2] + " was sent.");
    }

    public static void responseMsgInfo(String[] tokens) {
        Message msg = new Message(Arrays.copyOfRange(tokens, 2, 7));
        msg.printMsg();
    }

    public static void responseInboxInfo(String[] tokens) {
        System.out.println("Inbox: " + tokens[2]);
    }

    public static void responseSentInfo(String[] tokens) {
        System.out.println("Sent: " + tokens[2]);
    }

    public static void responseUserInfo(String[] tokens) {
        User user = new User(Arrays.copyOfRange(tokens, 2, 13));
        user.printUser();
    }

    public static void responseUserDeleted(String[] tokens) {
        System.out.println("User with id " + tokens[2] + " was deleted.");
    }

    public static void responseError(String[] tokens) {
        System.out.println("ERROR :: " + tokens[1] + " " + tokens[2]);
    }

    // main
    public static void main(String[] args) {

        // port
        int port = 5008;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // connection
        Socket socket;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        try {
            socket = new Socket("127.0.0.1", port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to the server.");
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("ERROR :: Could not connect to the server.");
            return;
        }

        // application
        String req;
        String resp;
        String[] tokens;

        // login
        System.out.println("Default user is 'superuser' with password '0000'.");
        while (true) {

            try {
                req = MyClient.requestLogin();
                outputStream.writeUTF(req);
                resp = inputStream.readUTF();
                tokens = resp.split("\\|", -1);

                if (tokens[0].equals("SUCCESSFUL") && tokens[1].equals("LOGGED_IN")) {
                    MyClient.responseLoggedIn(tokens);
                    break;
                } else if (tokens[0].equals("ERROR")) {
                    MyClient.responseError(tokens);
                }

            } catch (IOException e) {
                System.out.println("ERROR :: Connection lost.");
                try {
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    return;
                } catch (IOException ex) {
                    System.out.println("ERROR :: IOException");
                    return;
                }
            }
        }

        boolean noInterrupt = true;

        // while loop
        while (true) {

            req = "-";
            resp = "-";

            if (noInterrupt) {

                // take input from user
                boolean tryAgain;
                do {
                    tryAgain = false;
                    System.out.print(">>> ");

                    try {
                        switch (br.readLine().toUpperCase()) {
                            case "LOGOUT" -> req = MyClient.requestLogout();
                            case "HELP" -> {
                                tryAgain = true;
                                MyClient.printHelp();
                            }
                            case "QUIT" -> {
                                tryAgain = true;
                                MyClient.printQuit();
                            }
                            case "SENDMSG"    -> req = MyClient.requestSendMsg();
                            case "SHOWMSG"    -> req = MyClient.requestShowMsg();
                            case "SHOWINBOX"  -> req = MyClient.requestShowInbox();
                            case "SHOWSENT"   -> req = MyClient.requestShowSent();
                            case "CREATEUSER" -> req = MyClient.requestCreateUser();
                            case "DELETEUSER" -> req = MyClient.requestDeleteUser();
                            case "SHOWUSER"   -> req = MyClient.requestShowUser();
                            case "UPDATEUSER" -> req = MyClient.requestUpdateUser();
                            default -> {
                                tryAgain = true;
                                System.out.println("Invalid command. Type HELP to see commands.");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("ERROR :: Could not read your inputs. Try again.");
                    }
                } while (tryAgain);

                // send request to the server
                try {
                    outputStream.writeUTF(req);
                } catch (IOException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }

            // get respond from the server
            try {
                noInterrupt = true;
                resp = inputStream.readUTF();
                tokens = resp.split("\\|", -1);

                if (tokens[0].equals("SUCCESSFUL")) {
                    switch (tokens[1]) {
                        case "LOGGED_OUT"   -> MyClient.responseLoggedOut();
                        case "MSG_SENT"     -> MyClient.responseMsgSent(tokens);
                        case "MSG_INFO"     -> MyClient.responseMsgInfo(tokens);
                        case "INBOX_INFO"   -> MyClient.responseInboxInfo(tokens);
                        case "SENT_INFO"    -> MyClient.responseSentInfo(tokens);
                        case "USER_INFO"    -> MyClient.responseUserInfo(tokens);
                        case "USER_DELETED" -> MyClient.responseUserDeleted(tokens);
                        default             -> System.out.println("ERROR :: Invalid response from the server.");
                    }

                } else if (tokens[0].equals("ERROR")) {
                    switch (tokens[1]) {
                        // TODO: LOGIN_ERROR is redundant
                        case "LOGIN_ERROR", "LOGOUT_ERROR", "NO_AUTHORIZATION", "COMMAND_FAILED", "INVALID_COMMAND", "NOT_FOUND", "SQLEXCEPTION" -> MyClient.responseError(tokens);
                        default -> System.out.println("ERROR :: Invalid response from the server.");
                    }

                } else if (tokens[0].equals("WARNING")) {
                    if (tokens[1].equals("AUTHORIZATION")) {
                        noInterrupt = false;
                        req = MyClient.requestAuthorization();
                        outputStream.writeUTF(req);
                    }

                } else {
                    System.out.println("ERROR :: Invalid response from the server.");
                }

            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            // logout
            try {
                if (req.startsWith("LOGOUT") && resp.contains("SUCCESSFUL") && resp.contains("LOGGED_OUT")) {
                    System.out.println("Disconnecting from the server.");
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    break;
                }
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }
}
