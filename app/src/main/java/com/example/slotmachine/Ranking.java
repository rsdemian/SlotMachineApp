package com.example.slotmachine;

public class Ranking {
    private String id;
    public String nomePlayer;
    public int pontuacao;


    public Ranking() { }

    public Ranking(String nomePlayer, int pontuacao) {
        this.nomePlayer = nomePlayer;
        this.pontuacao = pontuacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomePlayer() {
        return nomePlayer;
    }

    public void setNomePlayer(String nomePlayer) {
        this.nomePlayer = nomePlayer;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public String toString() {
        return
                "Jogador: " + nomePlayer + " - " + "Pontuação: ["+pontuacao+"]";
    }
}
