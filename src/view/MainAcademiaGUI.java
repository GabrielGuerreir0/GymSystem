package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAcademiaGUI {
    private JButton logarButton;
    private JPanel Painel;

    public MainAcademiaGUI() {
        // Inicializa o botão (assumindo que você já o fez em seu arquivo .form)

        // Adiciona o ActionListener para o botão
        logarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cria uma nova janela para a tela de login
                JFrame loginFrame = new JFrame("Login");
                LoginGUI homeGUI = new LoginGUI();  // Cria a tela Home LoginGUI

                // Define o painel da LoginGUI como o conteúdo da nova janela
                loginFrame.setContentPane(homeGUI.getPanelMain());
                loginFrame.setSize(600, 400);
                loginFrame.setLocationRelativeTo(null); // Centraliza a janela
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o aplicativo ao fechar a janela
                loginFrame.setVisible(true);

                // Opcional: Esconde a janela atual (MainAcademiaGUI)
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(Painel);
                mainFrame.dispose(); // Ou use mainFrame.setVisible(false);
            }
        });

        // Criação da janela principal
        JFrame mainFrame = new JFrame("Main Academia");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setContentPane(Painel); // Usando o painel existente
        mainFrame.setLocationRelativeTo(null); // Centraliza a janela

        // Adiciona o botão ao painel
         // Certifique-se de que o botão já esteja no painel se foi criado no .form

        // Exibe a janela principal
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        // Cria a GUI principal
        SwingUtilities.invokeLater(() -> new MainAcademiaGUI());
    }
}
