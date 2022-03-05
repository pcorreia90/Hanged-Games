public class ServerMain {
    public static void main(String[] args) {
        if(args.length == 1) {
            Server newServer = new Server(args[0], null);
            newServer.start();
        }else if(args.length == 2){
            Server newServer = new Server(args[0], args[1]);
            newServer.start();
        }else {
            System.out.println("Usage: (number of players) (optional: path to txt file)");
        }
    }
}
