import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Scanner;

public class Cliente {

    private static ZookeeperHelper.Queue queue;
    ZookeeperHelper.Lock lock;
    ZookeeperHelper.Barrier barrier;
    ZookeeperHelper.Leader leader;

    volatile boolean connected = false;
    volatile boolean expired = false;

    public static void main(String args[]) throws KeeperException, InterruptedException {
        ZookeeperHelper.Queue q = new ZookeeperHelper.Queue("localhost", "/filaTeste");
        System.out.println("Insira um numero de 1 a 100:");
        Scanner scannerIn = new Scanner(System.in);
        String resposta = scannerIn.nextLine();
        colocarElementoNaFila(q,validaNumero(resposta));
        Thread.sleep(1000);
        int r = consomeElementoDaFila(q);
        System.out.println(r);
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
