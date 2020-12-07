package models;

import java.util.Random;

public class Pergunta {

    private String pergunta;
    private String opcoes;
    private int resposta;

    public Pergunta() {
    }

    public Pergunta(String pergunta, String opcoes, int resposta) {
        this.pergunta = pergunta;
        this.opcoes = opcoes;
        this.resposta = resposta;
    }

    public static Pergunta pergunta1() {
        return new Pergunta("Qual a capital do Brasil?","1) Brasilia \n2) São Paulo \n3) Curitiba\n4) Manaus\n",1);
    }

    public static Pergunta pergunta2() {
        return new Pergunta("Qual a moeda utilizada nos EUA?","1) Real \n2) Bitcoin \n3) Dolar\n4) Euro\n",3);
    }

    public static Pergunta pergunta3() {
        return new Pergunta("Qual é o esporte mais popular do Brasil?","1) Hóquei \n2) Basquete \n3) Curling\n4) Futebol \n",4);
    }

    public static Pergunta pergunta4() {
        return new Pergunta("Qual desses animais não é um mamífero?","1) Baleia \n2) Jacaré \n3) Macaco\n4) Canguru\n",2);
    }

    public static Pergunta pergunta5() {
        return new Pergunta("Qual dessas features não pertencem ao zookeeper?","1) Queue \n2) Barrier \n3) RMI\n4) Leader Election\n",3);
    }

    public static Pergunta consumirPerguntaAleatoria(){
        Pergunta[] perguntaLista = new Pergunta[]{pergunta1(),pergunta2(),pergunta3(),pergunta4(),pergunta5()};
        int rnd = new Random().nextInt(perguntaLista.length);
        return perguntaLista[rnd];
    }

    public static Pergunta consumirPerguntaPorIndice(int id){
        Pergunta[] perguntaLista = new Pergunta[]{pergunta1(),pergunta2(),pergunta3(),pergunta4(),pergunta5()};
        return perguntaLista[id-1];
    }



    public String getPergunta() {
        return pergunta;
    }

    public String getOpcoes() {
        return opcoes;
    }

    public int getResposta() {
        return resposta;
    }
}
