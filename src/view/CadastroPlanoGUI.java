package view;
import javax.swing.*;

public class CadastroPlanoGUI {
    private JPanel panelMain;
    private JTextField inputTipo;
    private JTextField inputValor;
    private JTextArea inputDescricao;
    private JButton cadastrarButton;
    private JButton cancelarButton;

    public CadastroPlanoGUI() {
        // Lógica para cadastrar o plano
        cadastrarButton.addActionListener(e -> {
            String tipo = inputTipo.getText();
            float valor = Float.parseFloat(inputValor.getText());
            String descricao = inputDescricao.getText();

            // Crie o objeto Plano e faça o salvamento
            JOptionPane.showMessageDialog(null, "Plano " + tipo + " cadastrado com sucesso!");
        });

        // Lógica para o botão cancelar
        cancelarButton.addActionListener(e -> {
            // Fechar ou limpar a janela
        });
    }

    // Retorna o painel principal
    public JPanel getPanelMain() {
        return panelMain;
    }
}
