package view;

import model.Boss;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Random;

/** Chefe que materializa cópias visuais com rotinas e trajetórias próprias. */
class AbstracaoBoss extends Boss implements BossBehavior {
    private static final int CLONE_COUNT = 3;
    private static final int SPECIAL_POWER_FRAMES = 1_875; // 30 s a 16 ms por quadro
    private static final int POSITION_CHANGE_FRAMES = 125;
    private static final int MELEE_RADIUS = 126;
    private static final int WINDUP_FRAMES = 24;
    private static final Color BODY_COLOR = new Color(235, 170, 55);

    private final double[] cloneX = new double[CLONE_COUNT];
    private final double[] cloneY = new double[CLONE_COUNT];
    private final Random random = new Random();
    private int attackFrame;
    private double wanderX = GamePanel.ARENA_CENTER_X + 125;
    private double wanderY = GamePanel.ARENA_TOP + 190;
    private int meleeCooldown;
    private int meleeWindup;
    private boolean healthRevealed;

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

    /** A barra fica oculta até o chefe original receber o primeiro acerto. */
    @Override
    public void damage() {
        healthRevealed = true;
        super.damage();
    }

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

        if (meleeWindup > 0) {
            float progress = 1.0f - (float) meleeWindup / WINDUP_FRAMES;
            int radius = (int) (MELEE_RADIUS * (0.55 + 0.45 * progress));
            g.setStroke(new BasicStroke(3.0f));
            g.setColor(new Color(255, 125, 65, 105 + (int) (110 * progress)));
            g.drawOval((int) cloneX[1] - radius, (int) cloneY[1] - radius,
                    radius * 2, radius * 2);
        }

        if (healthRevealed) {
            int originalX = (int) cloneX[0];
            int originalY = (int) cloneY[0];
            g.setColor(new Color(50, 20, 20));
            g.fillRect(originalX - 40, originalY + 38, 80, 7);
            g.setColor(new Color(255, 220, 70));
            g.fillRect(originalX - 40, originalY + 38, 80 * hp() / maxHp(), 7);
        }
        g.setStroke(previousStroke);
    }

    /** As cópias se movem e atacam de formas diferentes; a cada 30 s projetam uma grade. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        attackFrame++;
        double phase = attackFrame * 0.018;
        Fighter stationaryPlayer = game.stationaryTarget();
        Fighter chaseTarget = stationaryPlayer == null ? target : stationaryPlayer;

        // O chefe original percorre a arena em um arco amplo, em vez de ficar no alto.
        cloneX[0] = GamePanel.ARENA_CENTER_X + Math.sin(phase * 0.72) * 205;
        cloneY[0] = GamePanel.ARENA_TOP + 140 + Math.sin(phase * 1.08) * 62;

        // O falso segue o jogador escolhido como alvo.
        moveToward(1, chaseTarget.centerX(), chaseTarget.centerY(), 1.65);

        // O terceiro clone escolhe um novo destino aleatório de tempos em tempos.
        if (attackFrame % 150 == 1) {
            wanderX = GamePanel.ARENA_LEFT + 45
                    + random.nextDouble() * (GamePanel.ARENA_RIGHT - GamePanel.ARENA_LEFT - 90);
            wanderY = GamePanel.ARENA_TOP + 45
                    + random.nextDouble() * (GamePanel.ARENA_BOTTOM - GamePanel.ARENA_TOP - 90);
        }
        moveToward(2, wanderX, wanderY, 1.25);
        if (attackFrame % 84 == 0) {
            game.shootAbstractVolley(cloneX[2], cloneY[2], target);
        }

        // As rajadas mudam de faixa a cada dois segundos e atacam de outra origem.
        int barragePosition = (attackFrame / POSITION_CHANGE_FRAMES) % 4;
        double[] laneX = { GamePanel.ARENA_LEFT + 55, GamePanel.ARENA_RIGHT - 55,
                GamePanel.ARENA_CENTER_X - 135, GamePanel.ARENA_CENTER_X + 135 };
        double[] laneY = { GamePanel.ARENA_TOP + 185, GamePanel.ARENA_TOP + 185,
                GamePanel.ARENA_TOP + 225, GamePanel.ARENA_TOP + 225 };
        if (attackFrame % 72 == 0) {
            game.shootAbstractLane(laneX[barragePosition], laneY[barragePosition],
                    barragePosition % 2 == 0);
        }

        updateStationaryPunish(game, stationaryPlayer != null);

        // Poder próprio: os clones projetam uma grade de linhas lentas em sequência.
        if (attackFrame % SPECIAL_POWER_FRAMES == 0) {
            game.shootAbstractLattice(cloneX[0], cloneY[2]);
        }
    }

    private void moveToward(int clone, double targetX, double targetY, double speed) {
        double dx = targetX - cloneX[clone];
        double dy = targetY - cloneY[clone];
        double distance = Math.hypot(dx, dy);
        if (distance <= speed || distance == 0) {
            cloneX[clone] = targetX;
            cloneY[clone] = targetY;
            return;
        }
        cloneX[clone] += dx / distance * speed;
        cloneY[clone] += dy / distance * speed;
    }

    private void updateStationaryPunish(GamePanel game, boolean playerIsStationary) {
        if (meleeCooldown > 0) meleeCooldown--;
        if (meleeWindup > 0) {
            meleeWindup--;
            if (meleeWindup == 0) {
                game.hitPlayersInRadiusAt(cloneX[1], cloneY[1], MELEE_RADIUS, true);
                meleeCooldown = 84;
            }
        } else if (playerIsStationary && meleeCooldown == 0) {
            meleeWindup = WINDUP_FRAMES;
        }
    }
}
