import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import models.Jogador;
import models.Pergunta;
import org.apache.zookeeper.KeeperException;

public class Master {
    private static ServerSocket server;
    private static int port = 9876;
    private static ZookeeperHelper.Queue queue;
    int jogadores = 1;
    int numeroPergunta = 1;


    public static void main(String args[]) throws IOException, ClassNotFoundException, KeeperException, InterruptedException {
        server = new ServerSocket(port);
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFila();
        criaPrimeiroJogador();

        ZookeeperHelper.Lock lock = ZookeeperHelper.criaLock();
        ZookeeperHelper.Barrier barreira = new ZookeeperHelper.Barrier("localhost","/b1",3);
        boolean flag = barreira.enter();
        while(true){
            System.out.println("Esperando os jogadores.");
            try{
                boolean success = lock.lock();
                if (success) {
                    List<Integer> listaRespostas = new ArrayList<>();
                    for(int i=0;i<2;i++) {
                        listaRespostas.add(consomeElementoDaFila(q));
                    }
                    lock.computeResultados(listaRespostas, new Pergunta().pergunta1(), 2);
                } else {
                    while(true) {
                    }
                }
            } catch (KeeperException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    static int consomeElementoDaFila(ZookeeperHelper.Queue q) {
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

    private static void criaPrimeiroJogador() throws KeeperException, InterruptedException {
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        filaJogadores.produce(1);
    }

    private static int pegaNumeroJogadores() throws KeeperException, InterruptedException {
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        int jogadores = filaJogadores.consume();
        filaJogadores.produce(jogadores);
        return jogadores;
    }



}
