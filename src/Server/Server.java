package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORTNUM = 8081;
    private ServerSocket serverSocket;
    private Game game;

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

