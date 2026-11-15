package view;

import model.Boss;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

class EncapsulamentoBoss extends Boss implements BossBehavior {
    private static final int MELEE_RADIUS = 126;
    private static final int WINDUP_FRAMES = 24;
    private static final int COOLDOWN_FRAMES = 84;
    private int cooldown = COOLDOWN_FRAMES;
    private int windup;
    private double positionX = GamePanel.ARENA_CENTER_X;
    private double positionY = GamePanel.ARENA_CENTER_Y;

    /** Inicializa o chefe perseguidor no centro e configura o primeiro ciclo de ataque. */
    EncapsulamentoBoss() {
        super("ENCAPSULAMENTO", "A Caixa Selada", new Color(125, 175, 255), 28);
    }

    /** Retorna a coordenada horizontal atualizada pela perseguição ao alvo. */
    @Override
    public double x() {
        return positionX;
    }

    /** Retorna a coordenada vertical atualizada pela perseguição ao alvo. */
    @Override
    public double y() {
        return positionY;
    }

    /** Move o chefe em direção ao alvo e executa o ataque após o tempo de preparação. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        double dx = target.centerX() - positionX;
        double dy = target.centerY() - positionY;
        double distance = Math.hypot(dx, dy);
        if (distance > 1) {
            double step = Math.min(1.35, distance);
            positionX = Math.max(GamePanel.ARENA_LEFT + 35,
                    Math.min(GamePanel.ARENA_RIGHT - 35, positionX + dx / distance * step));
            positionY = Math.max(GamePanel.ARENA_TOP + 35,
                    Math.min(GamePanel.ARENA_BOTTOM - 35, positionY + dy / distance * step));
        }
        if (windup > 0) {
            windup--;
            if (windup == 0) {
                game.hitPlayersInRadius(this, MELEE_RADIUS, true);
                cooldown = COOLDOWN_FRAMES;
            }
            return;
        }

        if (cooldown > 0) {
            cooldown--;
        } else {
            windup = WINDUP_FRAMES;
        }
    }

    /** Destaca o chefe e mostra o alcance crescente enquanto prepara o golpe. */
    @Override
    public void draw(Graphics2D g) {
        int centerX = (int) x();
        int centerY = (int) y();
        Stroke previousStroke = g.getStroke();

        // Desenha o corpo e a barra usando x()/y(), pois Boss.draw usa suas coordenadas internas.
        int bodyRadius = 31;
        g.setColor(new Color(125, 175, 255));
        g.fillOval(centerX - bodyRadius, centerY - bodyRadius,
                bodyRadius * 2, bodyRadius * 2);
        g.setColor(new Color(35, 85, 145, 230));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(centerX - bodyRadius, centerY - bodyRadius,
                bodyRadius * 2, bodyRadius * 2);

        g.setColor(Color.WHITE);
        String label = concept();
        g.drawString(label, centerX - g.getFontMetrics().stringWidth(label) / 2, centerY - 40);
        g.setColor(new Color(50, 20, 20));
        g.fillRect(centerX - 40, centerY + 38, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect(centerX - 40, centerY + 38, 80 * hp() / maxHp(), 7);

        if (windup > 0) {
            float progress = 1.0f - (float) windup / WINDUP_FRAMES;
            int radius = (int) (MELEE_RADIUS * (0.55 + 0.45 * progress));
            g.setStroke(new BasicStroke(3.0f));
            g.setColor(new Color(115, 195, 255, 105 + (int) (110 * progress)));
            g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        g.setStroke(previousStroke);
    }
}
