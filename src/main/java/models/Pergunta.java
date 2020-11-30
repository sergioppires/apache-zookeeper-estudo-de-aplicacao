package models;

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

    public Pergunta pergunta1() {
        return new Pergunta("Escolha o numero 1","1) numero 1 \n2) numero 2 \n3) numero 3\n4) numero 4 \n",1);
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
