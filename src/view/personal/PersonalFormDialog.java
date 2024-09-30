package view.personal;

import dao.PersonalTrainerDAO;
import model.PersonalTrainer;

import javax.swing.*;
import java.awt.*;

public class PersonalFormDialog extends JDialog {
    private JTextField nomeField, cpfField, telefoneField, emailField, registroProfissionalField, especialidadeField;
    private PersonalTrainer personal;
    private PersonalTrainerDAO personalTrainerDAO;
    private boolean isCadastro;  // Verificar se é um cadastro ou edição

    public PersonalFormDialog(int personalId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        personalTrainerDAO = new PersonalTrainerDAO(); // Instanciar o DAO
        setTitle(isCadastro ? "Cadastrar Personal Trainer" : "Editar Personal Trainer");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Se não for cadastro, buscamos os dados do personal no banco
        if (!isCadastro) {
            personal = personalTrainerDAO.getPersonalTrainerById(personalId);
            if (personal == null) {
                JOptionPane.showMessageDialog(this, "Erro: Personal não encontrado!");
                dispose();
                return;
            }
        } else {
            personal = new PersonalTrainer();  // Para cadastro de novo personal
        }

        // Campos de texto para informações do personal
        add(new JLabel("Nome:"));
        nomeField = new JTextField(isCadastro ? "" : personal.getNome());
        add(nomeField);

        add(new JLabel("CPF:"));
        cpfField = new JTextField(isCadastro ? "" : personal.getCPF());
        add(cpfField);

        add(new JLabel("Telefone:"));
        telefoneField = new JTextField(isCadastro ? "" : personal.getTelefone());
        add(telefoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField(isCadastro ? "" : personal.getEmail());
        add(emailField);

        add(new JLabel("Registro Profissional:"));
        registroProfissionalField = new JTextField(isCadastro ? "" : personal.getRegistroProfissional());
        add(registroProfissionalField);

        add(new JLabel("Especialidade:"));
        especialidadeField = new JTextField(isCadastro ? "" : personal.getEspecialidade());
        add(especialidadeField);

        // Botões de ação
        JButton salvarButton = new JButton(isCadastro ? "Cadastrar" : "Salvar");
        JButton cancelarButton = new JButton("Cancelar");

        salvarButton.addActionListener(e -> salvarPersonal());
        cancelarButton.addActionListener(e -> dispose());

        add(salvarButton);
        add(cancelarButton);
    }

    private void salvarPersonal() {
        personal.setNome(nomeField.getText());
        personal.setCPF(cpfField.getText());
        personal.setTelefone(telefoneField.getText());
        personal.setEmail(emailField.getText());
        personal.setRegistroProfissional(registroProfissionalField.getText());
        personal.setEspecialidade(especialidadeField.getText());

        if (isCadastro) {
            // Adicionar novo personal trainer no banco de dados
            personalTrainerDAO.addPersonalTrainer(personal);
            JOptionPane.showMessageDialog(this, "Personal cadastrado com sucesso!");
        } else {
            // Atualizar personal existente no banco de dados
            personalTrainerDAO.updatePersonalTrainer(personal);
            JOptionPane.showMessageDialog(this, "Personal atualizado com sucesso!");
        }

        dispose();  // Fechar o dialog após salvar
    }
}
