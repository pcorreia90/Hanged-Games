
import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORTNUM = 8081;
    private ServerSocket serverSocket;
    private List<PlayerHandler> playerList = new ArrayList<>();
    private String path;
    private int numPlayers;
    private ArrayList<String> words;
    private String word;
    private boolean gameOver = false;

    public Server(String numP, String path) {
        this.numPlayers = Integer.parseInt(numP);
        this.path = path;
        words = readFile();
        try {
            serverSocket = new ServerSocket(PORTNUM);
        } catch (IOException e) {
            e.printStackTrace();
        }
        word = words.get((int)(Math.random() * words.size())).toUpperCase();
        String ip = null;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Tell you players to use the following command on their terminals: nc " + ip + " " + PORTNUM);
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(clientSocket);
                Thread newThread = new Thread(player);
                newThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tellEveryone(String message, PlayerHandler p) {
        for (PlayerHandler player : playerList) {
            try {
                PrintWriter writer = new PrintWriter(player.clientSocket.getOutputStream(), true);
                if (!p.equals(player)) {
                    writer.println(message);
                    writer.write(
                            " /$$     /$$                        /$$                                     /$$      \n" +
                            "|  $$   /$$/                       | $$                                    | $$      \n" +
                            " \\  $$ /$$//$$$$$$  /$$   /$$      | $$  /$$$$$$   /$$$$$$$  /$$$$$$       | $$      \n" +
                            "  \\  $$$$//$$__  $$| $$  | $$      | $$ /$$__  $$ /$$_____/ /$$__  $$      | $$      \n" +
                            "   \\  $$/| $$  \\ $$| $$  | $$      | $$| $$  \\ $$|  $$$$$$ | $$$$$$$$      |__/      \n" +
                            "    | $$ | $$  | $$| $$  | $$      | $$| $$  | $$ \\____  $$| $$_____/                \n" +
                            "    | $$ |  $$$$$$/|  $$$$$$/      | $$|  $$$$$$/ /$$$$$$$/|  $$$$$$$       /$$      \n" +
                            "    |__/  \\______/  \\______/       |__/ \\______/ |_______/  \\_______/      |__/      \n" +
                            "                                                                                     \n" +
                            "                                                                                     \n" +
                            "                                                                                     ");
                    writer.flush();
                    continue;
                }
                writer.write(
                        " /$$     /$$                                      /$$                 /$$      \n" + "|  $$   /$$/                                     |__/                | $$      \n" +
                        " \\  $$ /$$//$$$$$$  /$$   /$$       /$$  /$$  /$$ /$$ /$$$$$$$       | $$      \n" +
                        "  \\  $$$$//$$__  $$| $$  | $$      | $$ | $$ | $$| $$| $$__  $$      | $$      \n" +
                        "   \\  $$/| $$  \\ $$| $$  | $$      | $$ | $$ | $$| $$| $$  \\ $$      |__/      \n" +
                        "    | $$ | $$  | $$| $$  | $$      | $$ | $$ | $$| $$| $$  | $$                \n" +
                        "    | $$ |  $$$$$$/|  $$$$$$/      |  $$$$$/$$$$/| $$| $$  | $$       /$$      \n" +
                        "    |__/  \\______/  \\______/        \\_____/\\___/ |__/|__/  |__/      |__/      \n" +
                        "                                                                               \n" +
                        "                                                                               \n" +
                        "                                                                               ");

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        gameOver = true;
    }


    private ArrayList<String> readFile () {
        BufferedReader reader;
        try {
            if(this.path == null){
                InputStream in = getClass().getResourceAsStream("words.txt");
                reader = new BufferedReader(new InputStreamReader(in));
            }else {
                reader = new BufferedReader(new FileReader(this.path));
            }
                String line = "";
                words = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }

                System.out.println("Total word count: " + words.size());
                reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }


    public class PlayerHandler implements Runnable{
        private Socket clientSocket;
        private Prompt prompt;

        public PlayerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void start() {

            try {
                gameOver = false;
                BufferedWriter msgWriter = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
                PrintStream ps = new PrintStream(clientSocket.getOutputStream());
                this.prompt = new Prompt(clientSocket.getInputStream(), ps);

                // ask for name
                msgWriter.write(
                        " /$$   /$$                                                                                                                  \n" +
                                "| $$  | $$                                                                                                                  \n" +
                                "| $$  | $$  /$$$$$$  /$$$$$$$   /$$$$$$  /$$$$$$/$$$$   /$$$$$$  /$$$$$$$         /$$$$$$   /$$$$$$  /$$$$$$/$$$$   /$$$$$$ \n" +
                                "| $$$$$$$$ |____  $$| $$__  $$ /$$__  $$| $$_  $$_  $$ |____  $$| $$__  $$       /$$__  $$ |____  $$| $$_  $$_  $$ /$$__  $$\n" +
                                "| $$__  $$  /$$$$$$$| $$  \\ $$| $$  \\ $$| $$ \\ $$ \\ $$  /$$$$$$$| $$  \\ $$      | $$  \\ $$  /$$$$$$$| $$ \\ $$ \\ $$| $$$$$$$$\n" +
                                "| $$  | $$ /$$__  $$| $$  | $$| $$  | $$| $$ | $$ | $$ /$$__  $$| $$  | $$      | $$  | $$ /$$__  $$| $$ | $$ | $$| $$_____/\n" +
                                "| $$  | $$|  $$$$$$$| $$  | $$|  $$$$$$$| $$ | $$ | $$|  $$$$$$$| $$  | $$      |  $$$$$$$|  $$$$$$$| $$ | $$ | $$|  $$$$$$$\n" +
                                "|__/  |__/ \\_______/|__/  |__/ \\____  $$|__/ |__/ |__/ \\_______/|__/  |__/       \\____  $$ \\_______/|__/ |__/ |__/ \\_______/\n" +
                                "                               /$$  \\ $$                                         /$$  \\ $$                                  \n" +
                                "                              |  $$$$$$/                                        |  $$$$$$/                                  \n" +
                                "                               \\______/                                          \\______/                                   ");
                msgWriter.newLine();
                msgWriter.newLine();
                msgWriter.newLine();
                msgWriter.flush();
                StringInputScanner namePrompt = new StringInputScanner();
                namePrompt.setMessage("Enter your username:");
                String name = prompt.getUserInput(namePrompt);
                Thread.currentThread().setName(name);
                playerList.add(this);
                gameLogic(msgWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void gameLogic(BufferedWriter writer) throws IOException {

                System.out.println("Word to guess is: " + word);
                while(playerList.size() != numPlayers){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                boolean found;
                String guess;
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
                while (letters > 0 || gameOver) {
                    found = false;
                    StringBuilder hiddenWord = new StringBuilder();
                    for (int i = 0; i < lettersGuessed.length; i++) {
                        hiddenWord.append(lettersGuessed[i] + " ");
                    }
                    writer.newLine();
                    writer.newLine();
                    writer.newLine();
                    writer.write("Word to guess is : " + hiddenWord.toString());
                    writer.newLine();
                    writer.flush();

                    StringInputScanner messageP = new StringInputScanner();
                    messageP.setMessage("Enter your guess:");
                    String message = this.prompt.getUserInput(messageP);
                    guess = message.toUpperCase();


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
                        synchronized (word) {
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
                }
                tellEveryone("\n" + Thread.currentThread().getName() + " GUESSED THE WORD FIRST! IT WAS : " + word, this);
                System.exit(1);
            }
        }

        @Override
        public void run() {
            System.out.println("New client: "+ clientSocket);
            start();
        }
    }
}

