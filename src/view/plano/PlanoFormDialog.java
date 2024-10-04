package view.plano;

import dao.PlanoDAO;
import model.Plano;

import javax.swing.*;
import java.awt.*;

public class PlanoFormDialog extends JDialog {
    private JTextField tipoField, valorField, descricaoField;
    private Plano plano;
    private PlanoDAO planoDAO;
    private boolean isCadastro;
    private PlanoListPanel planoListPanel;

    public PlanoFormDialog(int planoId, boolean isCadastro, PlanoListPanel planoListPanel) {
        this.isCadastro = isCadastro;
        this.planoListPanel = planoListPanel;
        planoDAO = new PlanoDAO();
        setTitle(isCadastro ? "Cadastrar Plano" : "Editar Plano");
        setSize(600, 400);  // Aumentar tamanho da janela
        setLocationRelativeTo(null);
        setModal(true);

        // Layout principal
        getContentPane().setBackground(new Color(13, 12, 22));  // Fundo escuro
        setLayout(new BorderLayout(20, 20));  // BorderLayout com espaçamento

        // Painel central com campos do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(13, 12, 22));  // Mesmo fundo que a janela
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Espaçamento ao redor
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;  // Alinha os textos à direita

        // Verificar se é edição e carregar dados do Plano
        if (!isCadastro) {
            plano = planoDAO.getPlanoById(planoId);
            if (plano == null) {
                JOptionPane.showMessageDialog(this, "Erro: Plano não encontrado!");
                dispose();
                return;
            }
        } else {
            plano = new Plano();
        }

        // Adicionar campos com labels à direita e campos de texto maiores
        addFormRow("Tipo:", tipoField = criarTextField(isCadastro ? "" : plano.getTipo()), formPanel, gbc);
        addFormRow("Valor:", valorField = criarTextField(isCadastro ? "" : String.valueOf(plano.getValor())), formPanel, gbc);
        addFormRow("Descrição:", descricaoField = criarTextField(isCadastro ? "" : plano.getDescricao()), formPanel, gbc);

        // Painel dos botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(13, 12, 22));  // Fundo escuro também
        JButton salvarButton = criarBotao(isCadastro ? "Cadastrar" : "Salvar", new Color(137, 227, 119), new Dimension(120, 40));  // Botão maior
        JButton cancelarButton = criarBotao("Cancelar", new Color(241, 92, 92), new Dimension(120, 40));  // Botão maior

        salvarButton.addActionListener(e -> salvarPlano());
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

    private void salvarPlano() {
        try {
            plano.setTipo(tipoField.getText());
            plano.setValor(Double.parseDouble(valorField.getText()));
            plano.setDescricao(descricaoField.getText());

            if (isCadastro) {
                planoDAO.addPlano(plano);
                JOptionPane.showMessageDialog(this, "Plano cadastrado com sucesso!");
            } else {
                planoDAO.updatePlano(plano);
                JOptionPane.showMessageDialog(this, "Plano atualizado com sucesso!");
            }

            // Atualizar a lista de planos
            planoListPanel.carregarPlanos();

            dispose();  // Fechar o dialog após salvar

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro: Valor inválido.");
        }
    }
}
