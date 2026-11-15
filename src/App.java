import javax.swing.SwingUtilities;

import view.BulletHellGame;

public class App {
    public static void main(String[] args) throws Exception {
        // Garante que a interface gráfica seja criada dentro da Thread de Eventos do Swing
        SwingUtilities.invokeLater(() -> {
            new BulletHellGame().setVisible(true);
        });
    }
}
