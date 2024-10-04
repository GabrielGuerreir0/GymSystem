package view.aluno;

import dao.AlunoDAO;
import dao.PersonalTrainerDAO;
import dao.PlanoDAO;
import model.Aluno;
import model.PersonalTrainer;
import model.Plano;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlunoFormDialog extends JDialog {
    private JTextField nomeField, cpfField, telefoneField, emailField, dataInicioField, idadeField;
    private JComboBox<PersonalTrainer> personalComboBox;
    private JComboBox<Plano> planoComboBox;
    private Aluno aluno;
    private AlunoDAO alunoDAO;
    private boolean isCadastro;

    public AlunoFormDialog(int alunoId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        alunoDAO = new AlunoDAO();
        PlanoDAO planoDAO = new PlanoDAO();
        setTitle(isCadastro ? "Cadastrar Aluno" : "Editar Aluno");
        setSize(600, 600);  // Aumentado o tamanho geral da janela
        setLocationRelativeTo(null);
        setModal(true);

        // Layout principal
        getContentPane().setBackground(new Color(13, 12, 22));  // Fundo escuro
        setLayout(new BorderLayout(20, 20));  // BorderLayout com espaçamento

        // Painel com espaçamento do topo e da base
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(13, 12, 22));  // Mesmo fundo que a janela
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Espaçamento do topo e base
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;  // Alinha os textos à direita

        // Adicionar campos com aumento de largura
        addFormRow("Nome:", nomeField = criarTextField(isCadastro ? "" : aluno.getNome()), formPanel, gbc);
        addFormRow("CPF:", cpfField = criarTextField(isCadastro ? "" : aluno.getCPF()), formPanel, gbc);
        addFormRow("Telefone:", telefoneField = criarTextField(isCadastro ? "" : aluno.getTelefone()), formPanel, gbc);
        addFormRow("Email:", emailField = criarTextField(isCadastro ? "" : aluno.getEmail()), formPanel, gbc);
        addFormRow("Data de Início:", dataInicioField = criarTextField(isCadastro ? "" : new SimpleDateFormat("yyyy-MM-dd").format(aluno.getDataInicio())), formPanel, gbc);
        addFormRow("Idade:", idadeField = criarTextField(isCadastro ? "" : String.valueOf(aluno.getIdade())), formPanel, gbc);

        // Personal Trainer ComboBox
        addFormRow("Personal Trainer:", personalComboBox = new JComboBox<>(), formPanel, gbc);
        carregarPersonais();

        // Plano ComboBox
        addFormRow("Plano:", planoComboBox = new JComboBox<>(), formPanel, gbc);
        carregarPlanos(planoDAO);

        // Painel dos botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro também
        JButton salvarButton = criarBotao(isCadastro ? "Cadastrar" : "Salvar", new Color(137, 227, 119), new Dimension(120, 40));  // Botão maior
        JButton cancelarButton = criarBotao("Cancelar", new Color(241, 92, 92), new Dimension(120, 40));  // Botão maior

        salvarButton.addActionListener(e -> salvarAluno());
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

    // Método para carregar os personal trainers no ComboBox
    private void carregarPersonais() {
        PersonalTrainerDAO personalDAO = new PersonalTrainerDAO();
        List<PersonalTrainer> personalTrainers = personalDAO.getAllPersonalTrainers();
        for (PersonalTrainer personal : personalTrainers) {
            personalComboBox.addItem(personal);
        }
    }

    // Método para carregar os planos no ComboBox
    private void carregarPlanos(PlanoDAO planoDAO) {
        List<Plano> planos = planoDAO.getAllPlanos();
        for (Plano plano : planos) {
            planoComboBox.addItem(plano);
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

        // Obter o plano selecionado no ComboBox
        Plano plano = (Plano) planoComboBox.getSelectedItem();
        aluno.setPlano(plano);

        if (isCadastro) {
            alunoDAO.addAluno(aluno);
            JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso!");
        } else {
            alunoDAO.updateAluno(aluno);
            JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso!");
        }

        dispose();
    }
}
