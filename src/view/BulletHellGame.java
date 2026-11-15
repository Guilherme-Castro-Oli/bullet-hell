package view;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Janela Swing que abre a partida coordenada por GamePanel. */
public class BulletHellGame extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Configura a janela principal e instala o painel que contém a partida. */
    public BulletHellGame() {
        super("POO Hell — Arena dos Conceitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    /** Inicia a janela na thread de eventos do Swing. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BulletHellGame().setVisible(true));
    }
}