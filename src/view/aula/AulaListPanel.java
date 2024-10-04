package view.aula;

import dao.AulaDAO;
import model.Aula;
import view.aluno.AlunoListPanel;
import view.personal.PersonalListPanel;
import view.plano.PlanoListPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AulaListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AulaDAO aulaDAO;
    private JTextField searchField;

    public AulaListPanel() {
        aulaDAO = new AulaDAO(); // Instanciar o DAO para acesso ao banco de dados
        setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento geral entre os componentes

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Alinhar os botões de navegação à esquerda com espaçamento
        navPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espaçamento ao redor do painel de navegação

        // Botões de navegação estilizados
        JButton alunoButton = criarBotaoNavegacao("Lista de Alunos");
        JButton personalButton = criarBotaoNavegacao("Lista de Personais");
        JButton aulaButton = criarBotaoNavegacao("Lista de Aulas");
        JButton planoButton = criarBotaoNavegacao("Lista de Planos");

        navPanel.add(alunoButton);
        navPanel.add(personalButton);
        navPanel.add(aulaButton);
        navPanel.add(planoButton);

        // Adicionar o painel de navegação ao topo do layout
        add(navPanel, BorderLayout.NORTH);

        // Ações para alternar entre as telas
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Criar painel de busca e tabela
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento interno
        centerPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espaçamento em torno do painel central

        // Painel para o campo de busca
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10)); // Espaçamento entre o label e o campo de busca
        searchPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adicionar borda ao redor do campo de busca

        // Estilizar o campo de busca
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBackground(new Color(192, 192, 192));  // Cor de fundo do campo de busca (amarelo claro)
        searchField.setForeground(Color.BLACK); // Cor do texto no campo de busca
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding interno no campo de busca

        // Estilizar o label do campo de busca
        JLabel searchLabel = new JLabel("Buscar por Nome da Aula: ");
        searchLabel.setForeground(new Color(216, 132, 16)); // Cor amarela para o texto do label
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14)); // Fonte estilizada para o label

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Adicionar o painel de busca ao centro
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Adicionar um DocumentListener para atualizar a tabela a cada letra digitada
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarLista();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarLista();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarLista();
            }

            private void atualizarLista() {
                carregarDados(searchField.getText());  // Recarregar os dados filtrados por nome
            }
        });

        // Criar modelo da tabela (não editável)
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Data", "Duração (min)", "Personal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Todas as células são não editáveis
            }
        };

        // Criar a tabela
        table = new JTable(tableModel);
        table.getTableHeader().setBackground(new Color(13, 12, 22));
        table.getTableHeader().setForeground(new Color(216, 132, 16)); // Cor do cabeçalho da tabela
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permitir selecionar apenas uma linha por vez
        table.setBackground(new Color(44, 44, 44)); // Cor de fundo da tabela
        table.setForeground(Color.WHITE); // Cor do texto da tabela
        table.setRowHeight(25); // Altura das linhas para maior legibilidade

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(216, 132, 16), 1)); // Borda amarela ao redor da tabela

        // Adicionar a tabela na parte central do painel
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Adicionar o painel central (busca + tabela) ao layout
        add(centerPanel, BorderLayout.CENTER);

        // Botões para ações adicionais
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Espaçamento entre os botões
        buttonPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar borda ao redor dos botões

        JButton addButton = criarBotaoAcao("Cadastrar Aula", new Color(137, 227, 119));
        JButton deleteButton = criarBotaoAcao("Excluir Aula", new Color(241, 92, 92));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Adicionar o painel de botões ao sul
        add(buttonPanel, BorderLayout.SOUTH);

        // Ação para abrir a janela de cadastro ao clicar no botão
        addButton.addActionListener(e -> {
            AulaFormDialog dialog = new AulaFormDialog(-1, true);  // -1 significa que é uma nova aula
            dialog.setVisible(true);
            carregarDados(searchField.getText());  // Recarregar os dados após adicionar uma nova aula
        });

        // Ação para excluir aula selecionada
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int aulaId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                int confirm = JOptionPane.showConfirmDialog(null,
                        "Tem certeza que deseja excluir a aula selecionada?",
                        "Confirmação de Exclusão",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    aulaDAO.deleteAula(aulaId);  // Excluir a aula do banco
                    carregarDados(searchField.getText());  // Atualizar a tabela após a exclusão
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecione uma aula para excluir.");
            }
        });

        // Detectar duplo clique para abrir a janela de edição
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    int aulaId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                    AulaFormDialog dialog = new AulaFormDialog(aulaId, false);
                    dialog.setVisible(true);
                    carregarDados(searchField.getText());  // Recarregar os dados após possível edição
                }
            }
        });

        // Preencher a tabela com dados do banco de dados
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

    // Método para carregar os dados da lista de aulas
    private void carregarDados(String nome) {
        tableModel.setRowCount(0);  // Limpar a tabela antes de carregar os dados

        List<Aula> aulas = aulaDAO.getAulasByName(nome);
        for (Aula aula : aulas) {
            String personalName = (aula.getPersonalTrainer() != null) ? aula.getPersonalTrainer().getNome() : "Sem Personal";
            tableModel.addRow(new Object[]{aula.getId(), aula.getNomeAula(), aula.getData(), aula.getDuracao(), personalName});
        }
    }
}
