package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Writer implements Runnable{
    private Socket clientSocket;
    private PrintWriter writer;
    private Scanner scanner;

    public Writer(Socket clientSocket, Scanner scanner) {
        try {
            this.clientSocket = clientSocket;
            this.scanner = scanner;
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!clientSocket.isClosed()){
            String message = getMessage();
            writer.println(message);
        }
    }

    public String getMessage(){
        System.out.println("Message");
        return scanner.nextLine();
    }
}
