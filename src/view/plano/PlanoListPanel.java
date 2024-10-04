package view.plano;

import dao.PlanoDAO;
import model.Plano;
import view.aluno.AlunoListPanel;
import view.aula.AulaListPanel;
import view.personal.PersonalListPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlanoListPanel extends JPanel {
    private DefaultListModel<Plano> planosListModel;
    private JList<Plano> planosList;
    private PlanoDAO planoDAO;

    public PlanoListPanel() {
        planoDAO = new PlanoDAO();
        setLayout(new BorderLayout(10, 10)); // Adicionar espaçamento geral entre os componentes

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Espaçamento entre os botões de navegação
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

        add(navPanel, BorderLayout.NORTH);

        // Ações de navegação
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList());

        // Lista de planos com um ListCellRenderer personalizado
        planosListModel = new DefaultListModel<>();
        planosList = new JList<>(planosListModel);
        planosList.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Fonte mais limpa para a lista
        planosList.setBackground(Color.WHITE); // Cor de fundo branca
        planosList.setBorder(BorderFactory.createLineBorder(new Color(200, 221, 242), 1)); // Borda leve

        // Definir o ListCellRenderer para mostrar o tipo do plano
        planosList.setCellRenderer(new ListCellRenderer<Plano>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Plano> list, Plano value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value.getTipo(), JLabel.LEFT);
                label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Negrito no tipo do plano
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding interno
                if (isSelected) {
                    label.setBackground(new Color(0, 153, 204)); // Azul para o item selecionado
                    label.setForeground(Color.WHITE);
                    label.setOpaque(true);
                } else {
                    label.setBackground(Color.WHITE); // Fundo branco para itens não selecionados
                    label.setForeground(Color.BLACK);
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(planosList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Planos"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões de ação
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Espaçamento entre os botões
        buttonsPanel.setBackground(new Color(13, 12, 22)); // Fundo escuro
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adicionar borda ao redor dos botões

        JButton adicionarButton = criarBotaoAcao("Adicionar Plano", new Color(137, 227, 119));
        JButton editarButton = criarBotaoAcao("Editar Plano", new Color(255, 204, 102));
        JButton excluirButton = criarBotaoAcao("Excluir Plano", new Color(241, 92, 92));

        buttonsPanel.add(adicionarButton);
        buttonsPanel.add(editarButton);
        buttonsPanel.add(excluirButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        // Carregar lista de planos
        carregarPlanos();

        // Ações dos botões
        adicionarButton.addActionListener(e -> new PlanoFormDialog(0, true, this).setVisible(true));

        editarButton.addActionListener(e -> {
            Plano planoSelecionado = planosList.getSelectedValue();
            if (planoSelecionado != null) {
                new PlanoFormDialog(planoSelecionado.getId(), false, this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um plano para editar.");
            }
        });

        excluirButton.addActionListener(e -> {
            Plano planoSelecionado = planosList.getSelectedValue();
            if (planoSelecionado != null) {
                int resposta = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    planoDAO.deletePlano(planoSelecionado.getId());
                    carregarPlanos();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um plano para excluir.");
            }
        });
    }

    public void carregarPlanos() {
        List<Plano> planos = planoDAO.getAllPlanos();
        planosListModel.clear();
        for (Plano plano : planos) {
            planosListModel.addElement(plano);
        }
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
}
