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
        barreiraComecoGame.enter();
        System.out.println("Jogadores minimos conectados. O Jogo vai começar!");
        while(true){
            Pergunta pergunta = Pergunta.consumirPerguntaPorIndice(numeroPergunta);
            int numeroJogadores = 3;
            ZookeeperHelper.Barrier barreiraPergunta = new ZookeeperHelper.Barrier("localhost","/p"+numeroPergunta,4);
            barreiraPergunta.enter();
            numeroPergunta = numeroPergunta+1;
            List<Integer> listaRespostas = new ArrayList<>();
            for (int i = 0; i < numeroJogadores; i++) {
                listaRespostas.add(consomeElementoDaFilaRespostas(q));
            }
            computaResultados(listaRespostas, pergunta, jogadores);
            if(numeroPergunta==6) {
                try {
                    ZookeeperHelper.Lock lock = ZookeeperHelper.criaLock();
                    boolean success = lock.lock();
                    if (success) {
                        for (int i = 0; i < numeroJogadores; i++) {
                            listaRespostas.add(consomeElementoDaFilaRespostas(q));
                        }
                        lock.computeResultados(listaRespostas, pergunta, jogadores);
                    } else {
                        while (true) {
                        }
                    }
                    for (int i = 0; i <= 3; i++) {
                        System.out.println("Jogador " + i + "| Score: " + jogadores[i].getScore());
                    }
                    System.out.println("Fim de jogo");
                    System.exit(0);
                } catch (Exception e){
                    System.exit(0);
                }
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

    private static void computaResultados(List<Integer> respostas, Pergunta pergunta, Jogador[] players) throws InterruptedException {
        new Thread().sleep(10000);
        int respostaCorreta = pergunta.getResposta();
        respostas.forEach(resposta ->{
            int resp = 0;
            int jogador = 0;
            resp = resposta%10;
            jogador =  ((resposta-resp)/10)-1;

            if(respostaCorreta==resp){
                players[jogador].pontuar(1);
            }
        });

        for(int i=0;i<3;i++){
            System.out.println("Jogador "+ i + "| Score: " + players[i].getScore());
        }



    }

}
