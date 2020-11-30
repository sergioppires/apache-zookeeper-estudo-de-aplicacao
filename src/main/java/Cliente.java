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

    volatile boolean connected = false;
    volatile boolean expired = false;

    public static void main(String args[]) throws KeeperException, InterruptedException, IOException, ClassNotFoundException {
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFila();
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Pergunta pergunta = new Pergunta();
        ZookeeperHelper.Barrier barreira = new ZookeeperHelper.Barrier("localhost","/b1",3);
        System.out.println("Aguardando todos os jogadores se conectarem.");
        boolean flag = barreira.enter();
        System.out.println(pergunta.pergunta1().getPergunta());
        System.out.println(pergunta.pergunta1().getOpcoes());
        Scanner scannerIn = new Scanner(System.in);
        String answer = scannerIn.nextLine();
        colocarElementoNaFila(q, validaNumero(answer));
        System.out.println("Obrigado por jogar conosco! O Relatório será gerado pelo servidor");
    }

    private static int validaNumero(String resposta){
        return Integer.parseInt(resposta);
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

}
