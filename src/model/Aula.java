package model;

import java.util.Date;
import java.util.List;

public class Aula {
    private String nomeAula;
    private Date data;
    private int duracao; // minutos
    private PersonalTrainer personal;
    private List<Aluno> alunos;

    public Aula(String nomeAula, Date data, int duracao, PersonalTrainer personal) {
        this.nomeAula = nomeAula;
        this.data = data;
        this.duracao = duracao;
        this.personal = personal;
    }

    public void adicionarAluno(Aluno aluno) {
        alunos.add(aluno);
    }

    public void listarAlunos() {
        for (Aluno aluno : alunos) {
            System.out.println(aluno.nome);
        }
    }

    public String getNomeAula() {
        return nomeAula;
    }
}
