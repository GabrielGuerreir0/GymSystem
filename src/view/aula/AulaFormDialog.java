package view.aula;

import dao.AulaDAO;
import dao.PersonalTrainerDAO;
import dao.AlunoDAO;
import model.Aula;
import model.PersonalTrainer;
import model.Aluno;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AulaFormDialog extends JDialog {
    private JTextField nomeAulaField, dataField, duracaoField, buscaAlunoField;
    private JComboBox<PersonalTrainer> personalComboBox;
    private JList<String> alunosList;
    private JList<String> alunosDisponiveisList;
    private DefaultListModel<String> alunosListModel;
    private DefaultListModel<String> alunosDisponiveisModel;
    private List<Aluno> alunosDisponiveis;
    private List<Aluno> alunosNaAula;
    private Aula aula;
    private AulaDAO aulaDAO;
    private boolean isCadastro;
    private JButton adicionarAlunoButton;
    private JButton excluirAlunoButton;

    public AulaFormDialog(int aulaId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        aulaDAO = new AulaDAO();
        setTitle(isCadastro ? "Cadastrar Aula" : "Editar Aula");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setModal(true);

        // Layout principal
        getContentPane().setBackground(new Color(13, 12, 22));  // Fundo escuro
        setLayout(new BorderLayout(20, 20));

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Espaçamento ao redor
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;

        // Se não for cadastro, buscar os dados da aula
        if (!isCadastro) {
            aula = aulaDAO.getAulaById(aulaId);
            if (aula == null) {
                JOptionPane.showMessageDialog(this, "Erro: Aula não encontrada!");
                dispose();
                return;
            }
        } else {
            aula = new Aula();
        }

        // Campos do formulário
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addFormRow("Nome da Aula:", nomeAulaField = criarTextField(isCadastro ? "" : aula.getNomeAula()), formPanel, gbc);

        gbc.gridy++;
        addFormRow("Data (yyyy-MM-dd):", dataField = criarTextField(isCadastro ? "" : new SimpleDateFormat("yyyy-MM-dd").format(aula.getData())), formPanel, gbc);

        gbc.gridy++;
        addFormRow("Duração (min):", duracaoField = criarTextField(isCadastro ? "" : String.valueOf(aula.getDuracao())), formPanel, gbc);

        gbc.gridy++;
        personalComboBox = new JComboBox<>();
        carregarPersonais();
        addFormRow("Personal Trainer:", personalComboBox, formPanel, gbc);
        if (!isCadastro) {
            PersonalTrainer personal = aula.getPersonalTrainer();
            if (personal != null) {
                personalComboBox.setSelectedItem(personal);
            }
        }

        // Lista de alunos na aula
        gbc.gridy++;
        gbc.gridwidth = 1;
        addFormRow("Alunos na Aula:", new JScrollPane(alunosList = criarList(alunosListModel = new DefaultListModel<>())), formPanel, gbc);

        // Lista de alunos disponíveis
        gbc.gridx = 1;
        addFormRow("Alunos Disponíveis:", new JScrollPane(alunosDisponiveisList = criarList(alunosDisponiveisModel = new DefaultListModel<>())), formPanel, gbc);

        // Campo de busca de alunos disponíveis
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        addFormRow("Buscar Aluno Disponível:", buscaAlunoField = criarTextField(""), formPanel, gbc);

        // Botões para adicionar e remover alunos
        gbc.gridx = 1;
        gbc.gridy++;
        adicionarAlunoButton = criarBotao("Adicionar Aluno", new Color(137, 227, 119), new Dimension(140, 40));
        excluirAlunoButton = criarBotao("Excluir Aluno", new Color(241, 92, 92), new Dimension(140, 40));

        formPanel.add(adicionarAlunoButton, gbc);
        formPanel.add(excluirAlunoButton, gbc);

        carregarAlunosDisponiveis();
        if (!isCadastro) {
            carregarAlunosDaAula();
        }

        adicionarAlunoButton.addActionListener(e -> adicionarAlunoNaAula());
        excluirAlunoButton.addActionListener(e -> excluirAlunoDaAula());

        buscaAlunoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }
        });

        // Painel de botões de ação
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro também
        JButton salvarButton = criarBotao(isCadastro ? "Cadastrar" : "Salvar", new Color(137, 227, 119), new Dimension(140, 40));
        JButton cancelarButton = criarBotao("Cancelar", new Color(241, 92, 92), new Dimension(140, 40));

        salvarButton.addActionListener(e -> salvarAula());
        cancelarButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelarButton);
        buttonPanel.add(salvarButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Método para carregar os personal trainers no ComboBox
    private void carregarPersonais() {
        PersonalTrainerDAO personalDAO = new PersonalTrainerDAO();
        List<PersonalTrainer> personalTrainers = personalDAO.getAllPersonalTrainers();
        for (PersonalTrainer personal : personalTrainers) {
            personalComboBox.addItem(personal);
        }
    }

    // Método para carregar alunos que já estão na aula
    private void carregarAlunosDaAula() {
        AlunoDAO alunoDAO = new AlunoDAO();
        alunosNaAula = alunoDAO.getAlunosByAulaId(aula.getId());
        alunosListModel.clear();
        for (Aluno aluno : alunosNaAula) {
            alunosListModel.addElement(aluno.getNome());
        }
    }

    // Método para carregar todos os alunos disponíveis
    private void carregarAlunosDisponiveis() {
        AlunoDAO alunoDAO = new AlunoDAO();
        alunosDisponiveis = alunoDAO.getAllAlunos();
        alunosNaAula = alunoDAO.getAlunosByAulaId(aula.getId());

        alunosDisponiveisModel.clear();

        for (Aluno aluno : alunosDisponiveis) {
            boolean associado = false;
            for (Aluno alunoNaAula : alunosNaAula) {
                if (aluno.getId() == alunoNaAula.getId()) {
                    associado = true;
                    break;
                }
            }
            if (!associado) {
                alunosDisponiveisModel.addElement(aluno.getNome());
            }
        }
    }

    // Método para adicionar aluno na aula
    private void adicionarAlunoNaAula() {
        int selectedIndex = alunosDisponiveisList.getSelectedIndex();
        if (selectedIndex != -1) {
            Aluno alunoSelecionado = alunosDisponiveis.get(selectedIndex);
            if (!alunosListModel.contains(alunoSelecionado.getNome())) {
                alunosListModel.addElement(alunoSelecionado.getNome());
                alunosNaAula.add(alunoSelecionado);
                alunosDisponiveisModel.remove(selectedIndex);
            }
        }
    }

    // Método para excluir aluno da aula
    private void excluirAlunoDaAula() {
        int selectedIndex = alunosList.getSelectedIndex();
        if (selectedIndex != -1) {
            Aluno alunoRemovido = alunosNaAula.remove(selectedIndex);
            alunosListModel.remove(selectedIndex);
            alunosDisponiveisModel.addElement(alunoRemovido.getNome());
        }
    }

    // Método para salvar a aula
    private void salvarAula() {
        aula.setNomeAula(nomeAulaField.getText());

        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataField.getText());
            aula.setData(new Date(utilDate.getTime()));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida!");
            return;
        }

        aula.setDuracao(Integer.parseInt(duracaoField.getText()));
        PersonalTrainer personal = (PersonalTrainer) personalComboBox.getSelectedItem();
        aula.setPersonalTrainer(personal);

        AlunoDAO alunoDAO = new AlunoDAO();
        if (isCadastro) {
            aulaDAO.addAula(aula);
            JOptionPane.showMessageDialog(this, "Aula cadastrada com sucesso!");
        } else {
            aulaDAO.updateAula(aula);
            JOptionPane.showMessageDialog(this, "Aula atualizada com sucesso!");
        }

        alunoDAO.excluirAlunosDaAula(aula.getId());
        for (Aluno aluno : alunosNaAula) {
            alunoDAO.addAlunoNaAula(aluno.getId(), aula.getId());
        }

        dispose();
    }

    // Método para filtrar alunos disponíveis
    private void filtrarAlunosDisponiveis() {
        String filtro = buscaAlunoField.getText().trim().toLowerCase();
        alunosDisponiveisModel.clear();

        for (Aluno aluno : alunosDisponiveis) {
            if (aluno.getNome().toLowerCase().contains(filtro)) {
                alunosDisponiveisModel.addElement(aluno.getNome());
            }
        }
    }

    // Método para criar labels estilizados
    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(new Color(216, 132, 16));  // Cor amarela
        label.setFont(new Font("SansSerif", Font.BOLD, 14));  // Fonte em negrito e tamanho 14
        return label;
    }

    // Método para criar campos de texto estilizados
    private JTextField criarTextField(String valor) {
        JTextField textField = new JTextField(valor);
        textField.setBackground(new Color(217, 217, 217));  // Cor cinza claro
        textField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Espaçamento interno
        textField.setPreferredSize(new Dimension(250, 30));  // Aumentar a largura do campo
        return textField;
    }

    // Método para criar listas estilizadas
    private JList<String> criarList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(217, 217, 217));
        list.setBorder(BorderFactory.createLineBorder(new Color(200, 221, 242), 1));
        return list;
    }

    // Método para criar botões estilizados
    private JButton criarBotao(String texto, Color corFundo, Dimension tamanho) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(Color.BLACK);
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setPreferredSize(tamanho);
        botao.setBorder(BorderFactory.createLineBorder(corFundo.darker(), 2));
        return botao;
    }

    // Método para adicionar um rótulo e campo ao formulário
    private void addFormRow(String labelText, JComponent field, JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(criarLabel(labelText), gbc);
        gbc.gridx = 1;
        formPanel.add(field, gbc);
    }
}
