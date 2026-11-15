package view;

import model.Boss;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/** Chefe que materializa cópias visuais com rotinas e trajetórias próprias. */
class AbstracaoBoss extends Boss implements BossBehavior {
    private static final int CLONE_COUNT = 3;
    private static final int SPECIAL_POWER_FRAMES = 1_875; // 30 s a 16 ms por quadro
    private static final Color BODY_COLOR = new Color(235, 170, 55);

    private final double[] cloneX = new double[CLONE_COUNT];
    private final double[] cloneY = new double[CLONE_COUNT];
    private int attackFrame;

    AbstracaoBoss() {
        super("ABSTRAÇÃO", "O Molde Antigo", BODY_COLOR, 36);
        cloneX[0] = GamePanel.ARENA_CENTER_X;
        cloneY[0] = GamePanel.ARENA_TOP + 100;
        cloneX[1] = GamePanel.ARENA_CENTER_X - 125;
        cloneY[1] = GamePanel.ARENA_TOP + 165;
        cloneX[2] = GamePanel.ARENA_CENTER_X + 125;
        cloneY[2] = GamePanel.ARENA_TOP + 165;
    }

    /** A cópia original é a entidade que recebe dano; todas as cópias podem ser atingidas. */
    boolean isHitAt(double x, double y) {
        for (int i = 0; i < CLONE_COUNT; i++) {
            if (Math.hypot(x - cloneX[i], y - cloneY[i]) < 35) return true;
        }
        return false;
    }

    boolean isOriginalHitAt(double x, double y) {
        return Math.hypot(x - cloneX[0], y - cloneY[0]) < 35;
    }

    @Override
    public double x() { return cloneX[0]; }

    @Override
    public double y() { return cloneY[0]; }

    /** Desenha os três corpos com o mesmo molde; a barra de vida pertence à origem. */
    @Override
    public void draw(Graphics2D g) {
        Stroke previousStroke = g.getStroke();
        for (int i = 0; i < CLONE_COUNT; i++) {
            int x = (int) cloneX[i];
            int y = (int) cloneY[i];
            g.setColor(BODY_COLOR);
            g.fillRoundRect(x - 31, y - 31, 62, 62, 12, 12);
            g.setColor(new Color(255, 225, 150));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(x - 31, y - 31, 62, 62, 12, 12);
            g.setColor(Color.WHITE);
            String label = concept();
            g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2, y - 40);
        }

        int originalX = (int) cloneX[0];
        int originalY = (int) cloneY[0];
        g.setColor(new Color(50, 20, 20));
        g.fillRect(originalX - 40, originalY + 38, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect(originalX - 40, originalY + 38, 80 * hp() / maxHp(), 7);
        g.setStroke(previousStroke);
    }

    /** As cópias se movem e atacam de formas diferentes; a cada 30 s projetam uma grade. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        attackFrame++;
        double phase = attackFrame * 0.018;

        // A entidade original atravessa o alto da arena em movimento lateral.
        cloneX[0] = GamePanel.ARENA_CENTER_X + Math.sin(phase) * 145;
        cloneY[0] = GamePanel.ARENA_TOP + 105 + Math.sin(phase * 0.7) * 18;

        // A primeira cópia patrulha horizontalmente e solta uma fileira horizontal.
        cloneX[1] = GamePanel.ARENA_CENTER_X - 125 + Math.sin(phase * 1.35) * 65;
        cloneY[1] = GamePanel.ARENA_TOP + 190;
        if (attackFrame % 90 == 0) {
            game.shootAbstractLane(cloneX[1], cloneY[1], true);
        }

        // A segunda cópia sobe e desce; suas fileiras verticais cruzam as duas rotas.
        cloneX[2] = GamePanel.ARENA_CENTER_X + 125;
        cloneY[2] = GamePanel.ARENA_TOP + 190 + Math.sin(phase * 1.1) * 65;
        if (attackFrame % 110 == 0) {
            game.shootAbstractLane(cloneX[2], cloneY[2], false);
        }

        // Poder próprio: os clones projetam uma grade de linhas lentas em sequência.
        if (attackFrame % SPECIAL_POWER_FRAMES == 0) {
            game.shootAbstractLattice(cloneX[1], cloneY[2]);
        }
    }
}
