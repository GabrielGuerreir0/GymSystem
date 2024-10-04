package view.personal;

import dao.PersonalTrainerDAO;
import model.PersonalTrainer;
import view.aluno.AlunoListPanel;
import view.aula.AulaListPanel;
import view.plano.PlanoListPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PersonalListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private PersonalTrainerDAO personalTrainerDAO;
    private JTextField searchField;

    public PersonalListPanel() {
        personalTrainerDAO = new PersonalTrainerDAO();
        setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento geral entre os componentes

        // Estilização do painel de navegação
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Espaçamento entre os botões
        navPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar espaçamento em torno do painel de navegação

        // Botões de navegação estilizados
        JButton alunoButton = criarBotaoNavegacao("Lista de Alunos");
        JButton personalButton = criarBotaoNavegacao("Lista de Personais");
        JButton aulaButton = criarBotaoNavegacao("Lista de Aulas");
        JButton planoButton = criarBotaoNavegacao("Lista de Planos");

        navPanel.add(alunoButton);
        navPanel.add(personalButton);
        navPanel.add(aulaButton);
        navPanel.add(planoButton);

        add(navPanel, BorderLayout.NORTH);

        // Ações para alternar entre as telas
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Painel central para campo de busca e tabela
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10)); // Espaçamento interno
        centerPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar espaçamento em torno do painel central

        // Campo de busca estilizado
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10)); // Espaçamento entre o label e o campo de busca
        searchPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espaçamento ao redor do campo de busca

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBackground(new Color(192, 192, 192));  // Cor de fundo do campo de busca ajustada
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding interno no campo de busca

        // Estilizar label de busca
        JLabel searchLabel = new JLabel("Buscar por Nome: ");
        searchLabel.setForeground(new Color(216, 132, 16)); // Amarelo
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Atualizar a tabela a cada letra digitada
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { atualizarLista(); }
            @Override
            public void removeUpdate(DocumentEvent e) { atualizarLista(); }
            @Override
            public void changedUpdate(DocumentEvent e) { atualizarLista(); }

            private void atualizarLista() {
                carregarDados(searchField.getText());
            }
        });

        // Tabela estilizada
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Registro Profissional", "Especialidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.getTableHeader().setBackground(new Color(13, 12, 22));
        table.getTableHeader().setForeground(new Color(216, 132, 16));  // Estilo do cabeçalho da tabela
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Seleção única
        table.setBackground(new Color(44, 44, 44)); // Cor de fundo ajustada da tabela
        table.setForeground(Color.WHITE);  // Cor das células
        table.setRowHeight(25);  // Ajuste da altura das linhas para maior legibilidade
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(216, 132, 16), 1));  // Borda amarela ao redor da tabela
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Painel de botões de ação estilizados
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Adicionar espaçamento entre os botões
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espaçamento ao redor dos botões

        JButton addButton = criarBotaoAcao("Cadastrar Personal", new Color(137, 227, 119));
        JButton deleteButton = criarBotaoAcao("Excluir Personal", new Color(241, 92, 92));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ação de cadastrar novo personal
        addButton.addActionListener(e -> {
            PersonalFormDialog dialog = new PersonalFormDialog(-1, true);
            dialog.setVisible(true);
            carregarDados(searchField.getText());
        });

        // Ação de excluir personal selecionado
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int personalId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Tem certeza que deseja excluir o personal selecionado?",
                        "Confirmação de Exclusão",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    personalTrainerDAO.deletePersonalTrainer(personalId);
                    carregarDados(searchField.getText());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecione um personal para excluir.");
            }
        });

        // Ação para editar personal ao dar duplo clique
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    int personalId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    PersonalFormDialog dialog = new PersonalFormDialog(personalId, false);
                    dialog.setVisible(true);
                    carregarDados(searchField.getText());
                }
            }
        });

        // Carregar dados iniciais
        carregarDados("");
    }

    // Método para criar botões de navegação
    private JButton criarBotaoNavegacao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(13, 12, 22));  // Fundo escuro
        botao.setForeground(new Color(216, 132, 16));  // Amarelo
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(160, 40)); // Aumentar o tamanho dos botões
        return botao;
    }

    // Método para criar botões de ação
    private JButton criarBotaoAcao(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(Color.BLACK);
        botao.setPreferredSize(new Dimension(160, 40));  // Aumentar tamanho do botão para caber o texto completo
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setFocusPainted(false);
        return botao;
    }

    private void carregarDados(String nome) {
        tableModel.setRowCount(0);

        List<PersonalTrainer> personalTrainers = personalTrainerDAO.getPersonalTrainersByName(nome);
        for (PersonalTrainer personal : personalTrainers) {
            tableModel.addRow(new Object[]{personal.getId(), personal.getNome(), personal.getRegistroProfissional(), personal.getEspecialidade()});
        }
    }

    // Métodos para alternar entre as telas
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
}
