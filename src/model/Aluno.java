package model;

import java.sql.Date;

public class Aluno extends Pessoa {
    private Date dataInicio;  // Data de início do aluno na academia
    private PersonalTrainer personal;  // Nome do personal trainer
    private int idade;  // Idade do aluno
    private Plano plano;  // Plano associado ao aluno

    // Construtor que herda de Pessoa com parâmetros
    public Aluno(String nome, String CPF, String telefone, String email) {
        super(nome, CPF, telefone, email);
    }

    // Construtor vazio
    public Aluno() {
        super();  // Chama o construtor vazio da classe Pessoa
    }

    // Getters e Setters
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public PersonalTrainer getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalTrainer personal) {
        this.personal = personal;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }


}
