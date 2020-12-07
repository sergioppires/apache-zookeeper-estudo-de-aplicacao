package models;

public class Jogador {

    private int id;
    private String nome;
    private int score;
    private boolean lider = false;

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

    public void setScore(int score) { this.score = score; }

    public void pontuar(int ponto) { this.score = score+ponto; }

    public int getId() {
        return id;
    }

    public void tornarLider(){
        this.lider = true;
    }

    public void tirarLideranca(){
        this.lider = false;
    }

    public boolean isLider() {
        return lider;
    }
}
