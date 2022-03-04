package Server;

public class ServerMain {
    public static void main(String[] args) {
        if(args.length < 1) {
            Server newServer = new Server("2");
            newServer.start();
        }else {
            Server newServer = new Server(args[0]);
            newServer.start();
        }
    }
}
