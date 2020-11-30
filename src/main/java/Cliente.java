import models.Pergunta;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static ZookeeperHelper.Queue queue;
    ZookeeperHelper.Lock lock;
    ZookeeperHelper.Barrier barrier;
    ZookeeperHelper.Leader leader;

    volatile boolean connected = false;
    volatile boolean expired = false;

    public static void main(String args[]) throws KeeperException, InterruptedException, IOException, ClassNotFoundException {
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFila();
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Pergunta pergunta = new Pergunta();
        ZookeeperHelper.Barrier barreira = new ZookeeperHelper.Barrier("localhost","/b1",2);
        boolean flag = barreira.enter();
        while(true) {
            System.out.println(pergunta.pergunta1().getPergunta());
            System.out.println(pergunta.pergunta1().getOpcoes());
            Scanner scannerIn = new Scanner(System.in);
            String answer = scannerIn.nextLine();
            colocarElementoNaFila(q, validaNumero(answer));
            socket = new Socket(host.getHostName(), 9876);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(Integer.parseInt(answer));
            ois = new ObjectInputStream(socket.getInputStream());
            int digitoValidador = (Integer) ois.readObject();
            if (digitoValidador == 1) {
                System.out.println("Resposta correta");
            } else {
                System.out.println("Resposta errada. Nova tentativa em 2 segundos.");
            }
            ois.close();
            oos.close();
            Thread.sleep(2000);
        }

    }

    private static int validaNumero(String resposta){
        return Integer.parseInt(resposta);
    }

    public void process(WatchedEvent e) {
        System.out.println(e);
        if(e.getType() == Watcher.Event.EventType.None){
            switch (e.getState()) {
            case SyncConnected:
                connected = true;
                break;
            case Disconnected:
                connected = false;
                break;
            case Expired:
                expired = true;
                connected = false;
                System.out.println("Exiting due to session expiration");
            default:
                break;
            }
        }
    }

    boolean isConnected(){
        return connected;
    }

    boolean isExpired(){
        return expired;
    }

    static void colocarElementoNaFila(ZookeeperHelper.Queue q, int i) throws KeeperException, InterruptedException {
        try {
            q.produce(i);
        } catch (KeeperException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
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
