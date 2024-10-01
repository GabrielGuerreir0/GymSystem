package model;

import java.sql.Date;

public class Aluno extends Pessoa {
    private Date dataInicio;
    private PersonalTrainer personal;
    private int idade;
    private Plano plano;  // Plano associado ao aluno

    public Aluno(String nome, String CPF, String telefone, String email) {
        super(nome, CPF, telefone, email);
    }

    public Aluno() {
        super();
    }

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
