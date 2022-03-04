package Server;

import java.io.*;
import java.lang.management.PlatformLoggingMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORTNUM = 8081;
    private ServerSocket serverSocket;
    private Game game;
    private List<PlayerHandler> playerList = new ArrayList<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(PORTNUM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(clientSocket);
                Thread newThread = new Thread(player);
                newThread.start();
                game = new Game();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tellEveryone(String message) {
        for (PlayerHandler player : playerList) {
            try {
                PrintWriter writer = new PrintWriter(player.clientSocket.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public class PlayerHandler implements Runnable{
        private Socket clientSocket;
        private String message;

        public PlayerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void showPlayerMsg() {

            try {
                BufferedReader msgReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while(!clientSocket.isClosed()){
                    message = msgReader.readLine();
                    game.printMessage(message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            System.out.println("I'm a new handler");
            showPlayerMsg();
        }
    }
}

