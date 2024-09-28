package view;
import model.Plano;

import javax.swing.*;

public class CadastroAlunoGUI extends JFrame {
    private JPanel panelMain;
    private JTextField inputNome;
    private JComboBox<Plano> selecionaPlano;
    private JTextField inputCPF;
    private JTextField inputTelefone;
    private JTextField inputEmail;
    private JTextField inputIdade;
    private JButton cadastrarButton;
    private JButton cancelarButton;

    public CadastroAlunoGUI() {
        // Lógica do botão de cadastrar aluno
        cadastrarButton.addActionListener(e -> {
            String nome = inputNome.getText();
            String cpf = inputCPF.getText();
            String telefone = inputTelefone.getText();
            String email = inputEmail.getText();
            int idade = Integer.parseInt(inputIdade.getText());

            // Aqui você pode criar o objeto Aluno e salvar no banco de dados, etc.
            JOptionPane.showMessageDialog(null, "Aluno " + nome + " cadastrado com sucesso!");
        });

        // Lógica do botão de cancelar
        cancelarButton.addActionListener(e -> {
            // Limpa os campos ou fecha a janela
        });
    }

    // Retorna o painel principal
    public JPanel getPanelMain() {
        return panelMain;
    }
}
