package model;

public class Aluno extends Pessoa {
    private int idade;
    private Plano plano;

    public Aluno(String nome, String CPF, String telefone, String email, int idade, Plano plano) {
        super(nome, CPF, telefone, email);
        this.idade = idade;
        this.plano = plano;
    }

    public void seInscrever(Aula aula) {
        aula.adicionarAluno(this);
    }
}

