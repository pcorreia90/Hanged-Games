package Server;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.lang.management.PlatformLoggingMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORTNUM = 8081;
    private ServerSocket serverSocket;
    private List<PlayerHandler> playerList = new ArrayList<>();
    private String word = "Picha";
    private Prompt prompt;

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
                playerList.add(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tellEveryone(String message, PlayerHandler p) {
        for (PlayerHandler player : playerList) {
            try {
                PrintWriter writer = new PrintWriter(player.clientSocket.getOutputStream(), true);
                if(!p.equals(player)) {
                    writer.println(message);
                    writer.write("=====================\n");
                    writer.write(">>>>>  YOU LOSE  <<<<<\n");
                    writer.write("=====================\n");
                    writer.flush();
                    continue;
                }
                writer.write("=====================\n");
                writer.write(">>>>>  YOU WIN  <<<<<\n");
                writer.write("=====================\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public class PlayerHandler implements Runnable{
        private Socket clientSocket;
        private Prompt prompt;

        public PlayerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void showPlayerMsg() {

            try {
                BufferedWriter msgWriter = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
                PrintStream ps = new PrintStream(clientSocket.getOutputStream());
                this.prompt = new Prompt(clientSocket.getInputStream(), ps);

                // ask for name
                StringInputScanner namePrompt = new StringInputScanner();
                namePrompt.setMessage("Enter your username:");
                String name = prompt.getUserInput(namePrompt);
                Thread.currentThread().setName(name);
                cenas(msgWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cenas(BufferedWriter writer) throws IOException {
                boolean found;
                String guess = "";
                String[] lettersGuessed = new String[word.length()];

                int letters = word.length();

                for (int i = 0; i < lettersGuessed.length; i++) {
                    if (word.charAt(i) == ' ') {
                        lettersGuessed[i] = " ";
                        letters--;
                    } else if (word.charAt(i) == '-'){
                        lettersGuessed[i] = "-";
                        letters--;
                    } else {
                        lettersGuessed[i] = "_";
                    }
                }

            while(!clientSocket.isClosed()) {
                while (letters > 0) {
                    found = false;
                    StringBuilder hiddenWord = new StringBuilder();
                    for (int i = 0; i < lettersGuessed.length; i++) {
                        hiddenWord.append(lettersGuessed[i] + " ");
                    }

                    // _____,    _ = a

                    writer.write(hiddenWord.toString());
                    writer.newLine();
                    writer.flush();

                    StringInputScanner messageP = new StringInputScanner();
                    messageP.setMessage("Enter your guess:");
                    String message = this.prompt.getUserInput(messageP);
                    guess = message;


                    // Se escreveu + que uma letra, está a tentar adivinhar a palavra
                    if (guess.length() > 1) {
                        if (guess.equals(word)) {
                            break;
                        }
                        writer.write("Failed to guess word");
                        writer.newLine();
                        writer.flush();
                        continue;
                    }

                    // Se for só uma letra, vai verificar se existe e se ainda não acertou
                    if (word.indexOf(guess) > -1) {
                        // verificação se já encontrou letra
                        for (int i = 0; i < lettersGuessed.length; i++) {
                            if (lettersGuessed[i].equals(guess)) {
                                writer.write("You have already found that letter !");
                                writer.newLine();
                                writer.flush();
                                found = true;
                                break;
                            }
                        }

                        // se acertou a letra e ainda não a tinha descoberto
                        // então faz cenas
                        if (!found) {
                            for (int i = 0; i < lettersGuessed.length; i++) {
                                if (word.charAt(i) == guess.charAt(0)) {
                                    lettersGuessed[i] = guess;
                                    letters--;
                                }
                            }
                        }
                    }
                }
                tellEveryone("\n" + Thread.currentThread().getName() + " GUESSED THE WORD FIRST! IT WAS : " + word, this);
                System.exit(1);
            }
        }
        @Override
        public void run() {
            System.out.println("New client: "+ clientSocket);
            showPlayerMsg();
        }
    }
}

