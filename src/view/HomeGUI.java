package view;

import javax.swing.*;

public class HomeGUI {
    private JPanel panelMain;
    private JButton botaoAulas; // Certifique-se de que este botão existe no arquivo de design
    private JButton botaoTreinos;

    public HomeGUI() {
        // Inicialização dos botões, certifique-se de que isso seja feito corretamente
        botaoAulas = new JButton("Ver Aulas"); // Se estiver usando GUI Builder, este passo pode ser automático
        botaoTreinos = new JButton("Ver Treinos");

        // Adiciona os botões ao painel
        panelMain = new JPanel();
        panelMain.add(botaoAulas);
        panelMain.add(botaoTreinos);

        // Ação do botão de aulas
        botaoAulas.addActionListener(e -> JOptionPane.showMessageDialog(null, "Aulas Clicado!"));

        // Ação do botão de treinos
        botaoTreinos.addActionListener(e -> JOptionPane.showMessageDialog(null, "Treinos Clicado!"));
    }

    // Retorna o painel principal
    public JPanel getPanelMain() {
        return panelMain;
    }
}
