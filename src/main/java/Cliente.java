import models.Jogador;
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

    public static void main(String args[]) throws KeeperException, InterruptedException, IOException, ClassNotFoundException {
        ZookeeperHelper.Queue filaRespostas = ZookeeperHelper.criaFilaRespostas();
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        ZookeeperHelper.Queue filaPerguntas = ZookeeperHelper.criaFilaPerguntas();
        Jogador jogador = criarJogador(pegarIdJogador(filaJogadores));
        ZookeeperHelper.Barrier barreiraComecoGame = new ZookeeperHelper.Barrier("localhost","/b1",3);
        System.out.println("Aguardando todos os jogadores se conectarem.");
        barreiraComecoGame.enter();
        while(true){
            Thread.sleep(1000);
            int numeroJogadores = pegaNumeroJogadores();
           // int indice = filaPerguntas.consume();
            int indice = 1;
            ZookeeperHelper.Barrier barreiraPergunta = new ZookeeperHelper.Barrier("localhost","/p"+indice,3);
            barreiraPergunta.enter();
            Pergunta pergunta = Pergunta.consumirPerguntaPorIndice(indice);
            System.out.println(pergunta.getPergunta());
            System.out.println(pergunta.getOpcoes());
            Scanner scannerIn = new Scanner(System.in);
            String answer = scannerIn.nextLine();
            if(Integer.parseInt(answer)== pergunta.getResposta()){
                System.out.println("Você acertou!");
            } else {
                System.out.println("Você errou!");
            }
            colocarElementoNaFila(filaRespostas, validaNumero(answer));
            indice++;

        }
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

    static int pegarIdJogador(ZookeeperHelper.Queue filaJogadores) throws KeeperException, InterruptedException {
       int idJogador =  filaJogadores.consume();
        filaJogadores.produce(idJogador+1);
        return idJogador;
    }

    static Jogador criarJogador(int id){
        return new Jogador(id);
    }

    private static int pegaNumeroJogadores() throws KeeperException, InterruptedException {
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        int jogadores = filaJogadores.consume();
        filaJogadores.produce(jogadores);
        return jogadores;
    }

}
