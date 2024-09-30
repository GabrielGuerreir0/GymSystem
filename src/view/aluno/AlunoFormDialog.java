package view.aluno;

import dao.AlunoDAO;
import dao.PersonalTrainerDAO;
import model.Aluno;
import model.PersonalTrainer;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlunoFormDialog extends JDialog {
    private JTextField nomeField, cpfField, telefoneField, emailField, dataInicioField, idadeField;
    private JComboBox<PersonalTrainer> personalComboBox;  // ComboBox para selecionar o Personal Trainer
    private Aluno aluno;
    private AlunoDAO alunoDAO;
    private boolean isCadastro;  // Para verificar se é um cadastro ou edição

    public AlunoFormDialog(int alunoId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        alunoDAO = new AlunoDAO(); // Instanciar o DAO
        setTitle(isCadastro ? "Cadastrar Aluno" : "Editar Aluno");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Se não for cadastro, buscamos os dados do aluno no banco
        if (!isCadastro) {
            aluno = alunoDAO.getAlunoById(alunoId);
            if (aluno == null) {
                JOptionPane.showMessageDialog(this, "Erro: Aluno não encontrado!");
                dispose();
                return;
            }
        } else {
            aluno = new Aluno();  // Para cadastro de novo aluno
        }

        // Campos de texto para informações do aluno
        add(new JLabel("Nome:"));
        nomeField = new JTextField(isCadastro ? "" : aluno.getNome());
        add(nomeField);

        add(new JLabel("CPF:"));
        cpfField = new JTextField(isCadastro ? "" : aluno.getCPF());
        add(cpfField);

        add(new JLabel("Telefone:"));
        telefoneField = new JTextField(isCadastro ? "" : aluno.getTelefone());
        add(telefoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField(isCadastro ? "" : aluno.getEmail());
        add(emailField);

        add(new JLabel("Data de Início:"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dataInicioField = new JTextField(isCadastro ? "" : sdf.format(aluno.getDataInicio()));
        add(dataInicioField);

        add(new JLabel("Idade:"));
        idadeField = new JTextField(isCadastro ? "" : String.valueOf(aluno.getIdade()));
        add(idadeField);

        // ComboBox para selecionar o Personal Trainer
        add(new JLabel("Personal Trainer:"));
        personalComboBox = new JComboBox<>();
        carregarPersonais();  // Preencher o ComboBox com os personais disponíveis
        add(personalComboBox);

        if (!isCadastro) {
            // Selecionar o personal trainer atual no combo box
            PersonalTrainer personal = aluno.getPersonal();
            if (personal != null) {
                personalComboBox.setSelectedItem(personal);
            }
        }

        // Botões de ação
        JButton salvarButton = new JButton(isCadastro ? "Cadastrar" : "Salvar");
        JButton cancelarButton = new JButton("Cancelar");

        salvarButton.addActionListener(e -> salvarAluno());
        cancelarButton.addActionListener(e -> dispose());

        add(salvarButton);
        add(cancelarButton);
    }

    // Método para carregar os personal trainers no ComboBox
    private void carregarPersonais() {
        PersonalTrainerDAO personalDAO = new PersonalTrainerDAO();
        List<PersonalTrainer> personalTrainers = personalDAO.getAllPersonalTrainers();
        for (PersonalTrainer personal : personalTrainers) {
            personalComboBox.addItem(personal);  // Carregar os personais no ComboBox
        }
    }

    private void salvarAluno() {
        aluno.setNome(nomeField.getText());
        aluno.setCPF(cpfField.getText());
        aluno.setTelefone(telefoneField.getText());
        aluno.setEmail(emailField.getText());

        // Converter a data de início
        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataInicioField.getText());
            aluno.setDataInicio(new Date(utilDate.getTime()));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data de início inválida!");
            return;
        }

        aluno.setIdade(Integer.parseInt(idadeField.getText()));

        // Obter o personal selecionado no ComboBox
        PersonalTrainer personal = (PersonalTrainer) personalComboBox.getSelectedItem();
        aluno.setPersonal(personal);

        if (isCadastro) {
            // Adicionar novo aluno no banco de dados
            alunoDAO.addAluno(aluno);
            JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso!");
        } else {
            // Atualizar aluno existente no banco de dados
            alunoDAO.updateAluno(aluno);
            JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso!");
        }

        dispose();  // Fechar o dialog após salvar
    }
}
