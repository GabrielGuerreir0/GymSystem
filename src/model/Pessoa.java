package model;

public class Pessoa {
    protected String nome;
    protected String CPF;
    protected String telefone;
    protected String email;

    public Pessoa(String nome, String CPF, String telefone, String email) {
        this.nome = nome;
        this.CPF = CPF;
        this.telefone = telefone;
        this.email = email;
    }

    public void exibirInformacoes() {
        System.out.println("Nome: " + nome + ", CPF: " + CPF);
        // Outros dados
    }
}


