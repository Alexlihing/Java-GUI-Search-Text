import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * SearchClient
 *
 * Client that connects to a server and can search for text in a file
 *
 * @author Alex Li, section 05
 * @version 11/05/2024
 */

public class SearchClient {
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome! You will be directed " +
                "to the next \npop-up and can exit the program at any time.");

        String hostName = JOptionPane.showInputDialog(null, "Enter host " +
                "name", "Server Information", JOptionPane.QUESTION_MESSAGE);
        if (hostName == null) {
            JOptionPane.showMessageDialog(null, "You have exited the program, which will now be " +
                    "terminated.", "Exit", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // will be using port number 4242
        int portNumber = 0;
        try {
            String portString = JOptionPane.showInputDialog(null, "Enter port " +
                    "number", "Server Information", JOptionPane.QUESTION_MESSAGE);
            if (portString == null) {
                JOptionPane.showMessageDialog(null, "You have exited the program, " +
                        "which will now be terminated.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            portNumber = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            socket = new Socket(hostName, portNumber);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String text = JOptionPane.showInputDialog(null, "Enter text to search",
                        "Search", JOptionPane.QUESTION_MESSAGE);
                if (text == null) {
                    JOptionPane.showMessageDialog(null, "You have exited the program, which " +
                            "will now be terminated.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                writer.println(text);
                writer.flush();
                ArrayList<String> results = new ArrayList<>();
                String searchResult = "";
                while ((searchResult = reader.readLine()) != null) {
                    if (searchResult.equals("NO_MORE_ENTRIES"))
                        break;
                    results.add(searchResult);
                }

                String choice = null;
                if (results.size() == 0) {
                    JOptionPane.showMessageDialog(null, "No results found", "Error", JOptionPane.ERROR_MESSAGE);
                    writer.println("-1");
                } else {
                    choice = (String) JOptionPane.showInputDialog(null, "Select a page from the following results: ",
                            "Search Results", JOptionPane.QUESTION_MESSAGE, null, results.toArray(), results.get(0));
                    if (choice == null) {
                        JOptionPane.showMessageDialog(null, "You have exited the program, which " +
                                "will now be terminated.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    int index = results.indexOf(choice);
                    if (index == -1) {
                        JOptionPane.showMessageDialog(null, "Invalid selection.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                    writer.println(index);
                    writer.flush();
                    String returnedLine = reader.readLine();
                    JOptionPane.showMessageDialog(null, "Search Result: " + returnedLine,
                            "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
                int goAgain = JOptionPane.showConfirmDialog(null, "Would you like to search " +
                        "again?", "Continue", JOptionPane.YES_NO_OPTION);
                if (goAgain == JOptionPane.NO_OPTION || goAgain == JOptionPane.CLOSED_OPTION) {
                    writer.println("exit");
                    writer.flush();
                    break;
                } else {
                    writer.println("repeat");
                    writer.flush();
                }
            }
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Invalid host details", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(null, "Thank you for using the search engine!",
                "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }
}
