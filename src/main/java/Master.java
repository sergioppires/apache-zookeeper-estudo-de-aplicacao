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
    private static int numeroPergunta = 1;


    public static void main(String args[]) throws  KeeperException, InterruptedException {
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFilaRespostas();
        ZookeeperHelper.Queue filaPerguntas = ZookeeperHelper.criaFilaPerguntas();
        criaPrimeiroJogador();
        ZookeeperHelper.Barrier barreiraComecoGame = new ZookeeperHelper.Barrier("localhost","/b1",3);
        boolean flag = barreiraComecoGame.enter();
        while(true){
            System.out.println("Jogadores minimos conectados. O Jogo vai começar!");
            Pergunta pergunta = Pergunta.consumirPerguntaPorIndice(numeroPergunta);
            int numeroJogadores = 3;
            for(int i=1;i<numeroJogadores;i++){
                filaPerguntas.produce(numeroPergunta);
            }
            ZookeeperHelper.Barrier barreiraPergunta = new ZookeeperHelper.Barrier("localhost","/p"+numeroPergunta,3);
            numeroPergunta = numeroPergunta+1;
            ZookeeperHelper.Lock lock = ZookeeperHelper.criaLock();
            try{
                boolean success = lock.lock();
                if (success) {
                    List<Integer> listaRespostas = new ArrayList<>();
                    for(int i=0;i<numeroJogadores;i++) {
                        listaRespostas.add(consomeElementoDaFilaRespostas(q));
                    }
                    lock.computeResultados(listaRespostas, pergunta, numeroJogadores);
                } else {
                    while(true) {
                    }
                }
                if(numeroPergunta==5){
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

    private static int pegaNumeroJogadores() throws KeeperException, InterruptedException {
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        int jogadores = filaJogadores.consume();
        filaJogadores.produce(jogadores);
        return jogadores;
    }

    private static int pegaNumeroJogadoresBarrier() throws KeeperException, InterruptedException {
        int n = pegaNumeroJogadores();
        if(n<2){
            n=2;
        }
        return n;
    }



}
