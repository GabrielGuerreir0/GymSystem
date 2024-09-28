package model;

import java.util.List;

public class PersonalTrainer extends Pessoa {
    private String registroProfissional;
    private String especialidade;
    private List<Aula> aulas;

    public PersonalTrainer(String nome, String CPF, String telefone, String email, String registroProfissional, String especialidade) {
        super(nome, CPF, telefone, email);
        this.registroProfissional = registroProfissional;
        this.especialidade = especialidade;
    }

    public void criarAula(Aula aula) {
        aulas.add(aula);
    }

    public void listarAulas() {
        for (Aula aula : aulas) {
            System.out.println(aula.getNomeAula());
        }
    }
}
