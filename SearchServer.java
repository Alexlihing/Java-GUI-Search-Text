import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * SearchServer
 *
 * Server that reads from a file/database and performs searching operations
 *
 * @author Alex Li, section 05
 * @version 11/05/2024
 */

public class SearchServer {
    public static void main(String[] args) throws IOException {
        File f = new File("searchDatabase.txt");
        ArrayList<String> messages = new ArrayList<String>();
        //port is 4242
        ServerSocket serverSocket = new ServerSocket(4242);

        // remove later
        System.out.println("Server is listening on " + 4242);

        boolean clientConnected = false;
        while (true) {
            if (clientConnected)
                continue;

            clientConnected = true;
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");

            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                String message = "";

                while ((message = reader.readLine()) != null) {
                    try (BufferedReader bfr = new BufferedReader(new FileReader(f))) {
                        String line = "";
                        while ((line = bfr.readLine()) != null) {
                            if (line.contains(message)) {
                                messages.add(line);
                                line = line.substring(line.indexOf(";") + 1);
                                line = line.substring(0, line.indexOf(";"));
                                writer.println(line);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    writer.println("NO_MORE_ENTRIES");
                    String choiceString = reader.readLine();
                    if (choiceString == null)
                        continue;
                    int choice = Integer.parseInt(choiceString);

                    if (choice != -1) {
                        String temp = messages.get(choice);
                        temp = temp.substring(temp.indexOf(";") + 1);
                        temp = temp.substring(temp.indexOf(";") + 1);
                        writer.println(temp);
                    }

                    messages.clear();
                    String action = reader.readLine();

                    if (!action.equals("repeat")) {
                        clientConnected = false;
                    }
                }
            }
        }
    }
}
