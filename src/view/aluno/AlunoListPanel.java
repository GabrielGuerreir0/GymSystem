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
        setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento geral entre os componentes

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Espaçamento entre os botões de navegação
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

        // Ações de navegação
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Painel de busca e tabela
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento interno
        centerPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar espaçamento em torno do painel central

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10)); // Espaçamento entre o label e o campo de busca
        searchPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espaçamento ao redor do campo de busca

        // Estilizar o campo de busca
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBackground(new Color(192, 192, 192));  // Cor de fundo do campo de busca (amarelo claro)
        searchField.setForeground(Color.BLACK); // Cor do texto no campo de busca
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding interno no campo de busca

        // Estilizar o label do campo de busca
        JLabel searchLabel = new JLabel("Buscar por Nome: ");
        searchLabel.setForeground(new Color(216, 132, 16)); // Cor amarela para o texto do label
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14)); // Fonte estilizada para o label

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Adicionar DocumentListener para atualizar a tabela conforme o usuário digita
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
        table.getTableHeader().setBackground(new Color(13, 12, 22));
        table.getTableHeader().setForeground(new Color(216, 132, 16)); // Estilo do cabeçalho da tabela
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Seleção única
        table.setBackground(new Color(44, 44, 44)); // Cor de fundo ajustada da tabela
        table.setForeground(Color.WHITE); // Cor do texto
        table.setRowHeight(25);  // Altura das linhas para melhor legibilidade

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(216, 132, 16), 1)); // Borda amarela ao redor da tabela

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Botões de ação
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Espaçamento entre os botões
        buttonPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar borda ao redor dos botões

        JButton addButton = criarBotaoAcao("Cadastrar Aluno", new Color(137, 227, 119));
        JButton deleteButton = criarBotaoAcao("Excluir Aluno", new Color(241, 92, 92));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Ação para adicionar aluno
        addButton.addActionListener(e -> {
            AlunoFormDialog dialog = new AlunoFormDialog(-1, true);
            dialog.setVisible(true);
            carregarDados(searchField.getText());
        });

        // Ação para excluir aluno
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

        // Ação para editar aluno ao dar duplo clique
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
        botao.setPreferredSize(new Dimension(160, 40));  // Aumentar tamanho do botão
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setFocusPainted(false);
        return botao;
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
