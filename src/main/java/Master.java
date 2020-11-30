import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import models.Pergunta;
import org.apache.zookeeper.KeeperException;

public class Master {
    private static ServerSocket server;
    private static int port = 9876;
    private static ZookeeperHelper.Queue queue;


    public static void main(String args[]) throws IOException, ClassNotFoundException, KeeperException, InterruptedException {
        server = new ServerSocket(port);
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFila();
        ZookeeperHelper.Barrier barreira = new ZookeeperHelper.Barrier("localhost","/b1",2);
        while(barreira.leave()){
            System.out.println("Esperando os jogadores.");
            Thread.sleep(1000);
        }
        while(true){
            Socket socket = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            int message = (Integer) ois.readObject();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            if(message == consomeElementoDaFila(q)){
                oos.write(1);
            } else {
                oos.write(0);
            }
            ois.close();
            oos.close();
            socket.close();
            if(message == 0) break;
        }
        server.close();
    }



    private static int validaNumero(String resposta){
        return Integer.parseInt(resposta);
    }

    private static void lock(){
        ZookeeperHelper.Lock lock = new ZookeeperHelper.Lock("localhost","/lock",1000);

    }

    private static void barrier(){
        ZookeeperHelper.Barrier barrier = new ZookeeperHelper.Barrier("localhost","/filaTeste",3);
    }

    private static Pergunta criaPergunta(){
        return criaPergunta().pergunta1();
    }

    static int consomeElementoDaFila(ZookeeperHelper.Queue q) throws KeeperException, InterruptedException {
        int x = 0;
        try{
            x = q.consume();
        } catch (KeeperException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return x;
    }

}
