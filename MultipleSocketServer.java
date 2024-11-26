import java.net.*;
import java.io.*;
import java.util.Date;

public class MultipleSocketServer implements Runnable {
    private Socket connection;
    private String connectionTime;

    public static void main(String[] args) {
        int port = 6196;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("MultipleSocketServer Initialized on port " + port);
            while (true) {
                Socket connection = serverSocket.accept();
                Runnable runnable = new MultipleSocketServer(connection);
                new Thread(runnable).start();
            }
        } catch (IOException e) {
            System.err.println("Error initializing server: " + e.getMessage());
        }
    }

    MultipleSocketServer(Socket socket) {
        this.connection = socket;
        this.connectionTime = new Date().toString();
    }

    public void run() {
        try {
            System.out.println("Client connected at: " + connectionTime);

            // Odbieranie wiadomości
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder messageBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) { // Odczyt wielolinii
                messageBuffer.append(line).append("\n");
            }

            String fullMessage = messageBuffer.toString().trim();
            String[] parts = fullMessage.split(": ", 2); // Podział wiadomości na "userID: message"
            String userId = parts.length > 0 ? parts[0] : "Unknown User";
            String message = parts.length > 1 ? parts[1] : "No message";

            // Wyświetlanie w konsoli wiadomości i użytkownika
            System.out.println("Message received from User ID: " + userId);
            System.out.println("Message: " + message);

            // Logowanie wiadomości do pliku
            logMessage(userId, message);
            
             // Oczekiwanie  przed wysłaniem odpowiedzi
            Thread.sleep(10000);

            // Wysyłanie odpowiedzi
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "US-ASCII"));
            String response = "User ID: " + userId + " - Message received at: " + connectionTime +
                              " - Current time: " + new Date().toString() + "\r\n";
            writer.write(response);
            writer.flush();
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void logMessage(String userId, String message) {
        try {
            File logFile = new File(userId + ".log");
            try (FileWriter fw = new FileWriter(logFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {

                String timestamp = new Date().toString();
                out.println(timestamp + ":");
                out.println(message); // Dopisujemy całą wiadomość (wieloliniową)
                out.println(); // Dodajemy pustą linię dla przejrzystości
            }
        } catch (IOException e) {
            System.err.println("Error logging message: " + e.getMessage());
        }
    }
}
