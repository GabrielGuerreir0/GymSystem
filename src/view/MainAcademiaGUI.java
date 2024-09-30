package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAcademiaGUI {
    private JButton logarButton;
    private JPanel Painel;

    public MainAcademiaGUI() {
        // Adiciona o ActionListener para o botão de login
        logarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria uma nova janela para a tela de login
                JFrame loginFrame = new JFrame("Login");
                LoginGUI loginGUI = new LoginGUI();  // Cria a tela de login

                // Define o painel da LoginGUI como o conteúdo da nova janela
                loginFrame.setContentPane(loginGUI.getPanelMain());
                loginFrame.setSize(600, 400);
                loginFrame.setLocationRelativeTo(null); // Centraliza a janela
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o aplicativo ao fechar a janela
                loginFrame.setVisible(true);

                // Fecha a janela atual (MainAcademiaGUI)
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(Painel);
                mainFrame.dispose();
            }
        });

        // Criação da janela principal
        JFrame mainFrame = new JFrame("Main Academia");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setContentPane(Painel); // Usando o painel existente
        mainFrame.setLocationRelativeTo(null); // Centraliza a janela
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        // Cria a GUI principal
        SwingUtilities.invokeLater(() -> new MainAcademiaGUI());
    }
}
