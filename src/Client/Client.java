package Client;

import org.omg.CORBA.WrongTransaction;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Scanner scanner = new Scanner(System.in);
    private String hostname = getHost();
    private int portNum = Integer.parseInt(getPort());
    private Writer writer;
   // private Reader reader;

    private Socket socket;

    {
        try {
            socket = new Socket(hostname, portNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;
    public Client() {
        writer = new Writer(socket, scanner);
        writer.run();
        //reader = new Reader();


    }

    public String getHost(){
        System.out.println("Hostname");
        return scanner.nextLine();
    }

    public String getPort(){
        System.out.println("Port");
        return scanner.nextLine();
    }


}
