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
    private static int numeroPergunta = 1;




    public static void main(String args[]) throws  KeeperException, InterruptedException {
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFilaRespostas();

        Jogador jogador1 = new Jogador(1);
        Jogador jogador2 = new Jogador(2);
        Jogador jogador3 = new Jogador(3);
        Jogador[] jogadores = new Jogador[]{jogador1,jogador2,jogador3};

        criaPrimeiroJogador();
        ZookeeperHelper.Barrier barreiraComecoGame = new ZookeeperHelper.Barrier("localhost","/b1",4);
        boolean flag = barreiraComecoGame.enter();
        System.out.println("Jogadores minimos conectados. O Jogo vai começar!");
        while(true){
            Pergunta pergunta = Pergunta.consumirPerguntaPorIndice(numeroPergunta);
            int numeroJogadores = 3;
            ZookeeperHelper.Barrier barreiraPergunta = new ZookeeperHelper.Barrier("localhost","/p"+numeroPergunta,4);
            numeroPergunta = numeroPergunta+1;
            ZookeeperHelper.Lock lock = ZookeeperHelper.criaLock();
            try{
                boolean success = lock.lock();
                if (success) {
                    List<Integer> listaRespostas = new ArrayList<>();
                    for(int i=0;i<=numeroJogadores;i++) {
                        listaRespostas.add(consomeElementoDaFilaRespostas(q));
                    }
                    lock.computeResultados(listaRespostas, pergunta, jogadores);
                } else {
                    while(true) {
                    }
                }
                if(numeroPergunta==6){
                    for(int i=0;i<=3;i++){
                        System.out.println("Jogador "+ i + "| Score: " + jogadores[i].getScore());
                    }
                    System.out.println("Fim de jogo");
                    System.exit(0);
                }
                barreiraPergunta.enter();
            } catch (KeeperException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    static int consomeElementoDaFilaRespostas(ZookeeperHelper.Queue q) {
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

}
