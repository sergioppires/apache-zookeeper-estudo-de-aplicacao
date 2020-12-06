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
        ZookeeperHelper.Queue q = ZookeeperHelper.criaFila();
        ZookeeperHelper.Queue filaJogadores = ZookeeperHelper.criaFilaJogadores();
        Jogador jogador = criarJogador(pegarIdJogador(filaJogadores));


        Pergunta pergunta = Pergunta.consumirPerguntaAleatoria();
        ZookeeperHelper.Barrier barreira = new ZookeeperHelper.Barrier("localhost","/b1",3);
        System.out.println("Aguardando todos os jogadores se conectarem.");
        barreira.enter();
        System.out.println(pergunta.getPergunta());
        System.out.println(pergunta.getOpcoes());
        Scanner scannerIn = new Scanner(System.in);
        String answer = scannerIn.nextLine();
        colocarElementoNaFila(q, validaNumero(answer));
        System.out.println("Obrigado por jogar conosco! O Relatório será gerado pelo servidor");

        while(true){

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

}
