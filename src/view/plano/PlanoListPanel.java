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
        setLayout(new BorderLayout());

        // Criar o painel de navegação personalizado com botões
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Alinhar os botões de navegação à esquerda

        // Botões de navegação
        JButton alunoButton = new JButton("Lista de Alunos");
        JButton personalButton = new JButton("Lista de Personais");
        JButton aulaButton = new JButton("Lista de Aulas");
        JButton planoButton = new JButton("Lista de Planos"); // Adicionar o botão para a lista de planos

        // Adicionar os botões ao painel de navegação
        navPanel.add(alunoButton);
        navPanel.add(personalButton);
        navPanel.add(aulaButton);
        navPanel.add(planoButton); // Adicionar o botão de planos ao painel de navegação

        // Adicionar o painel de navegação ao topo do layout
        add(navPanel, BorderLayout.NORTH);

        // Adicionar ações para alternar entre as telas
        alunoButton.addActionListener(e -> trocarParaAlunoList());
        personalButton.addActionListener(e -> trocarParaPersonalList());
        aulaButton.addActionListener(e -> trocarParaAulaList());
        planoButton.addActionListener(e -> trocarParaPlanoList()); // Ação para alternar para o painel de lista de planos

        // Lista de planos com um ListCellRenderer personalizado
        planosListModel = new DefaultListModel<>();
        planosList = new JList<>(planosListModel);

        // Definir o ListCellRenderer para mostrar o tipo do plano
        planosList.setCellRenderer(new ListCellRenderer<Plano>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Plano> list, Plano value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value.getTipo()); // Mostrar o tipo do plano
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true); // Para o fundo ser visível
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(planosList);
        add(scrollPane, BorderLayout.CENTER);

        // Botões de ação
        JPanel buttonsPanel = new JPanel();
        JButton adicionarButton = new JButton("Adicionar Plano");
        JButton editarButton = new JButton("Editar Plano");
        JButton excluirButton = new JButton("Excluir Plano");

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
                planoDAO.deletePlano(planoSelecionado.getId());
                carregarPlanos();
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
        frame.setContentPane(new PlanoListPanel());  // Trocar para o painel de lista de planos
        frame.revalidate();
    }
}
