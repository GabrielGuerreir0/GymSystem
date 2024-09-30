package model;

public class PersonalTrainer extends Pessoa {
    private String registroProfissional;
    private String especialidade;

    public PersonalTrainer(String nome, String CPF, String telefone, String email, String registroProfissional, String especialidade) {
        super(nome, CPF, telefone, email);
        this.registroProfissional = registroProfissional;
        this.especialidade = especialidade;
    }

    // Construtor vazio para carregar personal trainers do banco
    public PersonalTrainer() {
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    // Sobrescrever toString para mostrar o nome no JComboBox
    @Override
    public String toString() {
        return getNome();  // Exibir o nome do personal no ComboBox e na lista de alunos
    }
}
