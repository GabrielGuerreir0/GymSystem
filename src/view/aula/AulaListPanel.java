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
        setLayout(new BorderLayout());

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Alinhar os botões de navegação à esquerda

        // Botões de navegação
        JButton alunoButton = new JButton("Lista de Alunos");
        JButton personalButton = new JButton("Lista de Personais");
        JButton aulaButton = new JButton("Lista de Aulas");
        JButton planoButton = new JButton("Lista de Planos"); // Botão de Lista de Planos

        // Adicionar os botões ao painel de navegação
        navPanel.add(alunoButton);
        navPanel.add(personalButton);
        navPanel.add(aulaButton);
        navPanel.add(planoButton);

        // Adicionar o painel de navegação ao topo do layout
        add(navPanel, BorderLayout.NORTH);

        // Adicionar ações para alternar entre as telas
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Criar painel de busca e tabela
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // Painel para o campo de busca
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchPanel.add(new JLabel("Buscar por Nome da Aula: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Adicionar o painel de busca ao centro
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Adicionar um DocumentListener para atualizar a tabela a cada letra digitada
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarLista();  // Atualizar a tabela quando uma letra for inserida
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarLista();  // Atualizar a tabela quando uma letra for removida
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarLista();  // Atualizar a tabela para mudanças no estilo (não aplicável ao texto simples)
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
        table.getTableHeader().setReorderingAllowed(false);  // Impedir que o usuário reorganize as colunas
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Permitir selecionar apenas uma linha por vez

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);  // Adicionar a tabela na parte central do centro

        // Adicionar o painel central (busca + tabela) ao layout
        add(centerPanel, BorderLayout.CENTER);

        // Botões para ações adicionais
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Cadastrar Aula");
        JButton deleteButton = new JButton("Excluir Aula");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH); // Adicionar o painel de botões ao sul

        // Ação para abrir a janela de cadastro ao clicar no botão
        addButton.addActionListener(e -> {
            // Abrir o formulário para cadastrar nova aula
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

                    // Abrir o dialog de edição (janela separada)
                    AulaFormDialog dialog = new AulaFormDialog(aulaId, false);
                    dialog.setVisible(true);
                    carregarDados(searchField.getText());  // Recarregar os dados após possível edição
                }
            }
        });

        // Preencher a tabela com dados do banco de dados
        carregarDados("");
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
    // Método para trocar para a tela de Lista de Planos
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
