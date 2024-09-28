package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI {
    private JPanel panelMain;
    private JTextField inputUsuario;
    private JPasswordField inputSenha;
    private JButton loginButton;

    public LoginGUI() {
        // Ação do botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = inputUsuario.getText();
                String senha = new String(inputSenha.getPassword());

                // Simulação de autenticação
                if (autenticarUsuario(usuario, senha)) {
                    // Se o login for bem-sucedido, abre a tela Home
                    JFrame homeFrame = new JFrame("Tela Principal");
                    HomeGUI homeGUI = new HomeGUI();  // Cria a tela Home
                    homeFrame.setContentPane(homeGUI.getPanelMain());
                    homeFrame.setSize(600, 400);
                    homeFrame.setLocationRelativeTo(null); // Centraliza a janela
                    homeFrame.setVisible(true);

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
        loginFrame.setSize(400, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null); // Centraliza a janela
        loginFrame.setVisible(true);
    }
}
