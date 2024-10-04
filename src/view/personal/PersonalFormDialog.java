package view.personal;

import dao.PersonalTrainerDAO;
import model.PersonalTrainer;

import javax.swing.*;
import java.awt.*;

public class PersonalFormDialog extends JDialog {
    private JTextField nomeField, cpfField, telefoneField, emailField, registroProfissionalField, especialidadeField;
    private PersonalTrainer personal;
    private PersonalTrainerDAO personalTrainerDAO;
    private boolean isCadastro;

    public PersonalFormDialog(int personalId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        personalTrainerDAO = new PersonalTrainerDAO();
        setTitle(isCadastro ? "Cadastrar Personal Trainer" : "Editar Personal Trainer");
        setSize(600, 600);  // Aumentar o tamanho da janela
        setLocationRelativeTo(null);
        setModal(true);

        // Layout principal
        getContentPane().setBackground(new Color(13, 12, 22));  // Fundo escuro
        setLayout(new BorderLayout(20, 20));  // BorderLayout com espaçamento

        // Painel central com espaçamento e campos de formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(13, 12, 22));  // Mesmo fundo que a janela
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Espaçamento ao redor
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;  // Alinha os textos à direita

        // Verificar se é edição e carregar dados do Personal Trainer
        if (!isCadastro) {
            personal = personalTrainerDAO.getPersonalTrainerById(personalId);
            if (personal == null) {
                JOptionPane.showMessageDialog(this, "Erro: Personal não encontrado!");
                dispose();
                return;
            }
        } else {
            personal = new PersonalTrainer();
        }

        // Adicionar campos com labels à direita e campos de texto maiores
        addFormRow("Nome:", nomeField = criarTextField(isCadastro ? "" : personal.getNome()), formPanel, gbc);
        addFormRow("CPF:", cpfField = criarTextField(isCadastro ? "" : personal.getCPF()), formPanel, gbc);
        addFormRow("Telefone:", telefoneField = criarTextField(isCadastro ? "" : personal.getTelefone()), formPanel, gbc);
        addFormRow("Email:", emailField = criarTextField(isCadastro ? "" : personal.getEmail()), formPanel, gbc);
        addFormRow("Registro Profissional:", registroProfissionalField = criarTextField(isCadastro ? "" : personal.getRegistroProfissional()), formPanel, gbc);
        addFormRow("Especialidade:", especialidadeField = criarTextField(isCadastro ? "" : personal.getEspecialidade()), formPanel, gbc);

        // Painel dos botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro também
        JButton salvarButton = criarBotao(isCadastro ? "Cadastrar" : "Salvar", new Color(137, 227, 119), new Dimension(120, 40));  // Botão maior
        JButton cancelarButton = criarBotao("Cancelar", new Color(241, 92, 92), new Dimension(120, 40));  // Botão maior

        salvarButton.addActionListener(e -> salvarPersonal());
        cancelarButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelarButton);
        buttonPanel.add(salvarButton);

        // Adicionar painel do formulário ao centro e painel dos botões ao sul
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Método para criar labels estilizados
    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(new Color(216, 132, 16));  // Cor amarela
        label.setFont(new Font("SansSerif", Font.BOLD, 14));  // Fonte em negrito e tamanho 14
        return label;
    }

    // Método para criar campos de texto estilizados e aumentados
    private JTextField criarTextField(String valor) {
        JTextField textField = new JTextField(valor);
        textField.setBackground(new Color(217, 217, 217));  // Cor cinza claro
        textField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Espaçamento interno
        textField.setPreferredSize(new Dimension(250, 30));  // Aumentar a largura do campo
        return textField;
    }

    // Método para criar botões estilizados e maiores
    private JButton criarBotao(String texto, Color corFundo, Dimension tamanho) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);  // Cor do fundo
        botao.setForeground(Color.BLACK);  // Cor do texto
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));  // Fonte em negrito
        botao.setFocusPainted(false);  // Remover o foco padrão
        botao.setPreferredSize(tamanho);  // Definir tamanho maior para o botão
        botao.setBorder(BorderFactory.createLineBorder(corFundo.darker(), 2));  // Borda personalizada
        return botao;
    }

    // Método para adicionar um rótulo e campo ao formulário
    private void addFormRow(String labelText, JComponent field, JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridy++;  // Próxima linha
        gbc.gridx = 0;  // Coluna 1: Label
        formPanel.add(criarLabel(labelText), gbc);
        gbc.gridx = 1;  // Coluna 2: Campo
        formPanel.add(field, gbc);
    }

    private void salvarPersonal() {
        personal.setNome(nomeField.getText());
        personal.setCPF(cpfField.getText());
        personal.setTelefone(telefoneField.getText());
        personal.setEmail(emailField.getText());
        personal.setRegistroProfissional(registroProfissionalField.getText());
        personal.setEspecialidade(especialidadeField.getText());

        if (isCadastro) {
            personalTrainerDAO.addPersonalTrainer(personal);
            JOptionPane.showMessageDialog(this, "Personal cadastrado com sucesso!");
        } else {
            personalTrainerDAO.updatePersonalTrainer(personal);
            JOptionPane.showMessageDialog(this, "Personal atualizado com sucesso!");
        }

        dispose();
    }
}
