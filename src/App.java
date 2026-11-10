import javax.swing.SwingUtilities;

import view.BulletHellGame;
import view.ScreenSaver;

public class App {
    public static void main(String[] args) throws Exception {
        // Garante que a interface gráfica seja criada dentro da Thread de Eventos do Swing
        SwingUtilities.invokeLater(() -> {
            ScreenSaver game = new ScreenSaver();
            game.start();
            //new BulletHellGame().setVisible(true);
        });
    }
}
