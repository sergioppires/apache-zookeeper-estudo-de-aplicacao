import java.util.ArrayList;
import java.util.List;

import models.Jogador;
import models.Pergunta;
import org.apache.zookeeper.KeeperException;

public class Master {
    private static int numeroPergunta = 1;
    public static int clienteLider;
    public static ZookeeperHelper.Leader leader;
    public static Jogador jogador1 = new Jogador(1);
    public static Jogador jogador2 = new Jogador(2);
    public static Jogador jogador3 = new Jogador(3);
    public static Jogador[] jogadores = new Jogador[]{jogador1,jogador2,jogador3};

    public static void main(String args[]) throws  KeeperException, InterruptedException {
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFilaRespostas();
        leader = new ZookeeperHelper.Leader("localhost","/election","/leader",0);

        Jogador jogador1 = new Jogador(1);
        Jogador jogador2 = new Jogador(2);
        Jogador jogador3 = new Jogador(3);
        jogadores = new Jogador[]{jogador1,jogador2,jogador3};

        ZookeeperHelper.Lock lock = ZookeeperHelper.criaLock();
        criaPrimeiroJogador();
        ZookeeperHelper.Barrier barreiraComecoGame = new ZookeeperHelper.Barrier("localhost","/b1",4);
        barreiraComecoGame.enter();
        System.out.println("Jogadores minimos conectados. O Jogo vai começar!");
        int numeroJogadores = 3;

        while(true){
            Pergunta pergunta = Pergunta.consumirPerguntaPorIndice(numeroPergunta);
            ZookeeperHelper.Barrier barreiraPergunta = new ZookeeperHelper.Barrier("localhost","/p"+numeroPergunta,4);
            barreiraPergunta.enter();
            numeroPergunta = numeroPergunta+1;
            List<Integer> listaRespostas = new ArrayList<>();
            for (int i = 0; i < numeroJogadores; i++) {
                listaRespostas.add(consomeElementoDaFilaRespostas(q));
            }
            computaResultados(listaRespostas, pergunta, jogadores);
            validaLideranca(jogadores);
            if(numeroPergunta==6) {
                try {
                    boolean success = lock.lock();
                    if (success) {
                        lock.computeResultados(jogadores);
                    } else {
                        while (true) {
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        System.out.println("Jogador " + i+1 + " | Score: " + jogadores[i].getScore());
                    }
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

    public static ZookeeperHelper.Leader getLeaderElection(){
        return leader;
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

    private static void validaLideranca(Jogador[] jogadores) throws KeeperException, InterruptedException {
        int lider;
        if(jogadores[0].getScore() > jogadores[1].getScore() && jogadores[0].getScore() > jogadores[2].getScore()){
            lider = 0;
        } else if(jogadores[1].getScore() > jogadores[0].getScore() && jogadores[1].getScore() > jogadores[2].getScore()){
            lider = 1;
        } else{
            lider=2;
        }
        clienteLider = lider;
    }

    public static int getLeader(){
        return clienteLider;
    }

    public static Jogador[] retornarJogadores(){
        return jogadores;
    }

    private static int validaLiderancaid(Jogador[] jogadores) {
        int lider;
        if(jogadores[0].getScore() > jogadores[1].getScore() && jogadores[0].getScore() > jogadores[2].getScore()){
            lider = 0;
        } else if(jogadores[1].getScore() > jogadores[0].getScore() && jogadores[1].getScore() > jogadores[2].getScore()){
            lider = 1;
        } else{
            lider=2;
        }
        return lider;
    }

}
