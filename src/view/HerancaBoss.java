package view;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Chefe que usa dois padrões alternados de rajada para representar herança.
 * A implementação reaproveita o estado e o contrato comum de Boss, implementa
 * BossBehavior para fornecer seu próprio ciclo de ataque e delega ao painel a
 * criação dos projéteis, mantendo separadas as regras do chefe e da arena.
 */
class HerancaBoss extends GameBoss implements BossBehavior {
    /** Cadência normal, em quadros da simulação, entre os ataques deste chefe. */
    private static final int NORMAL_ATTACK_INTERVAL = 54;
    /** Cadência da fase final, ativada quando a vida chega a quarenta por cento. */
    private static final int ENRAGED_ATTACK_INTERVAL = 40;
    /** Relógio que determina quando a próxima rajada está pronta. */
    private int attackFrame;
    /** Alternador que escolhe qual dos dois padrões será usado na próxima rajada. */
    private int volleyIndex;

    /** Inicializa identidade, cor e total de vida do primeiro chefe. */
    HerancaBoss() {
        super("HERANÇA", "O Descendente", new Color(70, 190, 100), 30);
    }

    /** Desenha a folha animada do chefe, mantendo o rótulo e a vida legíveis ao redor dela. */
    @Override
    public void draw(Graphics2D g) {
        if (defeated()) {
            return;
        }

        int centerX = (int) x();
        int centerY = (int) y();
        if (!BossSprites.draw(g, BossSprites.Type.HERANCA, centerX, centerY,
                attackFrame, 112)) {
            g.setColor(color);
            g.fillRect(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }

        g.setColor(Color.WHITE);
        String label = concept();
        g.drawString(label, centerX - g.getFontMetrics().stringWidth(label) / 2, centerY - 64);
        g.setColor(new Color(50, 20, 20));
        g.fillRect(centerX - 40, centerY + 56, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect(centerX - 40, centerY + 56, 80 * hp() / maxHp(), 7);
    }

    /** Alterna padrões mais densos e acelera a cadência quando o chefe perde vida. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        // A fase final encurta ainda mais o intervalo sem interromper o ciclo do painel.
        boolean enraged = hp() <= maxHp() * 0.4;
        int attackInterval = enraged ? ENRAGED_ATTACK_INTERVAL : NORMAL_ATTACK_INTERVAL;
        if (++attackFrame < attackInterval) {
            return;
        }
        attackFrame = 0;
        // A paridade mantém uma alternância previsível entre projétil divisor e leque.
        volleyIndex++;
        if (enraged) {
            // Um tiro central ainda se divide; os demais mantêm a leitura do leque final.
            game.shootFan(this, target, 7, 1.28, true, false,
                    3.45, new Color(95, 220, 120));
        } else if (volleyIndex % 2 == 1) {
            game.shootFan(this, target, 1, 0, true, false,
                    3.1, new Color(95, 220, 120));
        } else {
            // A rajada mantém um único divisor central para não multiplicar todos os cinco tiros.
            game.shootFan(this, target, 5, 0.68, true, false,
                    3.25, new Color(95, 220, 120));
        }
    }
}
