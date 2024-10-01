package view.aluno;

import dao.AlunoDAO;
import model.Aluno;
import view.aula.AulaListPanel;
import view.personal.PersonalListPanel;
import view.plano.PlanoListPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlunoListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AlunoDAO alunoDAO;
    private JTextField searchField;

    public AlunoListPanel() {
        alunoDAO = new AlunoDAO(); // Instanciar o DAO para acesso ao banco de dados
        setLayout(new BorderLayout());

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Botões de navegação
        JButton alunoButton = new JButton("Lista de Alunos");
        JButton personalButton = new JButton("Lista de Personais");
        JButton aulaButton = new JButton("Lista de Aulas");
        JButton planoButton = new JButton("Lista de Planos");

        navPanel.add(alunoButton);
        navPanel.add(personalButton);
        navPanel.add(aulaButton);
        navPanel.add(planoButton);

        add(navPanel, BorderLayout.NORTH);

        // Ações de navegação
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Painel de busca e tabela
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchPanel.add(new JLabel("Buscar por Nome: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                carregarDados(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                carregarDados(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                carregarDados(searchField.getText());
            }
        });

        // Criar modelo da tabela
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Data de Início", "Idade", "Personal", "Plano"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Criar a tabela
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Botões de ação
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Cadastrar Aluno");
        JButton deleteButton = new JButton("Excluir Aluno");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Ações para adicionar e excluir aluno
        addButton.addActionListener(e -> {
            AlunoFormDialog dialog = new AlunoFormDialog(-1, true);
            dialog.setVisible(true);
            carregarDados(searchField.getText());
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int alunoId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Tem certeza que deseja excluir o aluno selecionado?",
                        "Confirmação de Exclusão",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    alunoDAO.deleteAluno(alunoId);
                    carregarDados(searchField.getText());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecione um aluno para excluir.");
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    int alunoId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                    AlunoFormDialog dialog = new AlunoFormDialog(alunoId, false);
                    dialog.setVisible(true);
                    carregarDados(searchField.getText());
                }
            }
        });

        carregarDados("");
    }

    private void trocarParaAlunoList() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new AlunoListPanel());
        frame.revalidate();
    }

    private void trocarParaPersonalList() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new PersonalListPanel());
        frame.revalidate();
    }

    private void trocarParaAulaList() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new AulaListPanel());
        frame.revalidate();
    }

    private void trocarParaPlanoList() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new PlanoListPanel());
        frame.revalidate();
    }

    private void carregarDados(String nome) {
        tableModel.setRowCount(0);
        List<Aluno> alunos = alunoDAO.getAlunosByName(nome);
        for (Aluno aluno : alunos) {
            String personalName = (aluno.getPersonal() != null) ? aluno.getPersonal().getNome() : "Sem Personal";
            String planoTipo = (aluno.getPlano() != null) ? aluno.getPlano().getTipo() : "Sem Plano";
            tableModel.addRow(new Object[]{aluno.getId(), aluno.getNome(), aluno.getDataInicio(), aluno.getIdade(), personalName, planoTipo});
        }
    }
}
