package view;

import view.aluno.AlunoListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI {
    private JPanel panelMain;
    private JTextField inputUsuario;
    private JPasswordField inputSenha;
    private JButton loginButton;
    private JLabel Logo;

    public LoginGUI() {
        // Carregar a logo e definir no JLabel
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("../images/Captura de tela 2023-11-27 075813.png"));
            Image originalImage = originalIcon.getImage();
            // Redimensiona a imagem (exemplo para 100x100 pixels)
            Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            Logo.setIcon(resizedIcon); // Define a imagem redimensionada no JLabel
        } catch (Exception e) {
            System.out.println("Erro ao carregar a imagem da logo: " + e.getMessage());
        }

        // Ação do botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = inputUsuario.getText();
                String senha = new String(inputSenha.getPassword());

                // Simulação de autenticação
                if (autenticarUsuario(usuario, senha)) {
                    // Se o login for bem-sucedido, abre a tela de Alunos
                    JFrame alunoFrame = new JFrame("Lista de Alunos");
                    AlunoListPanel alunoListPanel = new AlunoListPanel();  // Cria o painel de Alunos

                    // Definir o painel da AlunoListPanel como o conteúdo da nova janela
                    alunoFrame.setContentPane(alunoListPanel);
                    alunoFrame.setSize(800, 600);
                    alunoFrame.setLocationRelativeTo(null); // Centraliza a janela
                    alunoFrame.setVisible(true);

                    // Fecha a tela de login após o sucesso
                    SwingUtilities.getWindowAncestor(panelMain).dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário ou senha inválidos!");
                }
            }
        });
    }

    // Método de autenticação simples (pode ser substituído por autenticação real)
    private boolean autenticarUsuario(String usuario, String senha) {
        return usuario.equals("admin") && senha.equals("1234");  // Exemplo simples
    }

    // Retorna o painel principal
    public JPanel getPanelMain() {
        return panelMain;
    }

    // Método principal para testar a tela de login
    public static void main(String[] args) {
        JFrame loginFrame = new JFrame("Login");
        LoginGUI loginGUI = new LoginGUI();
        loginFrame.setContentPane(loginGUI.getPanelMain());
        loginFrame.setSize(600, 400);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null); // Centraliza a janela
        loginFrame.setVisible(true);
    }
}
