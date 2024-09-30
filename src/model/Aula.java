package model;

import java.sql.Date;

public class Aula {
    private int id;
    private String nomeAula;
    private Date data;  // Usando java.sql.Date para o banco de dados
    private int duracao; // em minutos
    private PersonalTrainer personalTrainer;

    public Aula(String nomeAula, Date data, int duracao, PersonalTrainer personalTrainer) {
        this.nomeAula = nomeAula;
        this.data = data;
        this.duracao = duracao;
        this.personalTrainer = personalTrainer;
    }

    public Aula() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeAula() {
        return nomeAula;
    }

    public void setNomeAula(String nomeAula) {
        this.nomeAula = nomeAula;
    }

    public Date getData() {
        return data;
    }

    // Método que aceita java.util.Date e converte para java.sql.Date
    public void setData(java.util.Date utilDate) {
        this.data = new java.sql.Date(utilDate.getTime());
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public PersonalTrainer getPersonalTrainer() {
        return personalTrainer;
    }

    public void setPersonalTrainer(PersonalTrainer personalTrainer) {
        this.personalTrainer = personalTrainer;
    }
}
