package model;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Um projétil guarda posição, direção e velocidade.
 * O campo fromBoss permite que a tela saiba se o tiro deve ferir um jogador
 * ou o chefe, sem precisar de duas classes de projétil diferentes.
 */
public class Projectile {
    // vx e vy são quanto o projétil avança em cada atualização do jogo.
    private double x, y, vx, vy;
    private final boolean fromBoss;
    private Color color;
    private final int radius;
    private int age = 0;
    private final boolean splits, changesTruth;
    private boolean splitDone = false, harmful = true;

    /** Calcula uma direção normalizada entre a origem e o alvo. */
    public Projectile(double x, double y, double targetX, double targetY,
                      double speed, boolean fromBoss, Color color, int radius) {
        this(x, y, targetX, targetY, speed, fromBoss, color, radius, false, false);
    }

    /** Opções extras ativam a divisão do tiro ou a troca entre real e falso. */
    public Projectile(double x, double y, double targetX, double targetY,
                      double speed, boolean fromBoss, Color color, int radius,
                      boolean splits, boolean changesTruth) {
        this.x = x; this.y = y;
        double dx = targetX - x, dy = targetY - y;
        // hypot calcula a distância. O mínimo 1 evita divisão por zero
        // caso origem e alvo estejam exatamente no mesmo ponto.
        double length = Math.max(1, Math.hypot(dx, dy));
        // Dividimos pela distância para manter a velocidade igual em qualquer direção.
        vx = dx / length * speed;
        vy = dy / length * speed;
        this.fromBoss = fromBoss; this.color = color; this.radius = radius;
        this.splits = splits; this.changesTruth = changesTruth;
    }

    /** Avança o projétil um quadro. */
    public void update() {
        x += vx; y += vy; age++;
        // A cada 18 quadros, o tiro alterna entre verdadeiro (vermelho) e falso (roxo).
        if (changesTruth && age % 18 == 0) {
            harmful = !harmful;
            color = harmful ? new Color(255, 75, 65) : new Color(170, 100, 255);
        }
    }
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int)x - radius, (int)y - radius, radius * 2, radius * 2);
    }
    public boolean fromBoss() { return fromBoss; }
    public boolean isHarmful() { return harmful; }
    /** Informa uma única vez quando o tiro especial já está pronto para se dividir. */
    public boolean consumeSplitReady() {
        if (splits && !splitDone && age >= 32) { splitDone = true; return true; }
        return false;
    }
    public double x() { return x; }
    public double y() { return y; }
    /** Diz se já saiu da tela para a lista poder removê-lo e poupar memória. */
    public boolean outside(int w, int h) { return x < -20 || x > w + 20 || y < 30 || y > h + 20; }
}
