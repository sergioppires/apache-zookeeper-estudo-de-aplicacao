import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {
    private static ServerSocket server;
    private static int port = 9876;

    public static void main(String args) throws IOException, ClassNotFoundException {
        server = new ServerSocket(port);
        while(true){
            System.out.println("Waiting for the client request");
            Socket socket = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("Hi Client "+message);
            ois.close();
            oos.close();
            socket.close();
            if(message.equalsIgnoreCase("exit")) break;
        }
        server.close();
    }

    private static void lock(){
        ZookeeperHelper.Lock lock = new ZookeeperHelper.Lock("localhost","/lock",1000);

    }

    private static void barrier(){
        ZookeeperHelper.Barrier barrier = new ZookeeperHelper.Barrier("localhost","/filaTeste",10);
    }

}
