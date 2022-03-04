package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class Game implements Runnable{
        private String message;

    public Game(String message){
        this.message = message;
    }

    public void printMessage() {
       StringReader reader = new StringReader(message);
        try {
            System.out.println("Game: " + reader.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        printMessage();
    }
}
