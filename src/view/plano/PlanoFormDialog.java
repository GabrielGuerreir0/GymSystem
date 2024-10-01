package view.plano;

import dao.PlanoDAO;
import model.Plano;

import javax.swing.*;
import java.awt.*;

public class PlanoFormDialog extends JDialog {
    private JTextField tipoField, valorField, descricaoField;
    private Plano plano;
    private PlanoDAO planoDAO;
    private boolean isCadastro;  // Verificar se é cadastro ou edição
    private PlanoListPanel planoListPanel; // Referência ao painel de lista de planos

    public PlanoFormDialog(int planoId, boolean isCadastro, PlanoListPanel planoListPanel) {
        this.isCadastro = isCadastro;
        this.planoListPanel = planoListPanel;
        planoDAO = new PlanoDAO(); // Instanciar o DAO
        setTitle(isCadastro ? "Cadastrar Plano" : "Editar Plano");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Se não for cadastro, buscamos os dados do plano no banco
        if (!isCadastro) {
            plano = planoDAO.getPlanoById(planoId);
            if (plano == null) {
                JOptionPane.showMessageDialog(this, "Erro: Plano não encontrado!");
                dispose();
                return;
            }
        } else {
            plano = new Plano();  // Para cadastro de novo plano
        }

        // Campos de texto para informações do plano
        add(new JLabel("Tipo:"));
        tipoField = new JTextField(isCadastro ? "" : plano.getTipo());
        add(tipoField);

        add(new JLabel("Valor:"));
        valorField = new JTextField(isCadastro ? "" : String.valueOf(plano.getValor()));
        add(valorField);

        add(new JLabel("Descrição:"));
        descricaoField = new JTextField(isCadastro ? "" : plano.getDescricao());
        add(descricaoField);

        // Botões de ação
        JButton salvarButton = new JButton(isCadastro ? "Cadastrar" : "Salvar");
        JButton cancelarButton = new JButton("Cancelar");

        salvarButton.addActionListener(e -> salvarPlano());
        cancelarButton.addActionListener(e -> dispose());

        add(salvarButton);
        add(cancelarButton);
    }

    private void salvarPlano() {
        try {
            plano.setTipo(tipoField.getText());
            plano.setValor(Double.parseDouble(valorField.getText()));
            plano.setDescricao(descricaoField.getText());

            if (isCadastro) {
                // Adicionar novo plano no banco de dados
                planoDAO.addPlano(plano);
                JOptionPane.showMessageDialog(this, "Plano cadastrado com sucesso!");
            } else {
                // Atualizar plano existente no banco de dados
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
