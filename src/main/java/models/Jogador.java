package models;

public class Jogador {

    private int id;
    private String nome;
    private int score;
    private int it = 1;

    public Jogador(int id) {
        this.id = id;
        this.nome = "Jogador " + id;
        this.score = 0;
    }

    public String getNome() {
        return nome;
    }

    public int getScore() {
        return score;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
