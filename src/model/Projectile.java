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
    private final boolean splits;
    private final boolean changesTruth;
    private final Color color;
    private final int radius;
    private int frames;
    private boolean splitReady;
    private boolean harmful = true;

    /** Opções extras ativam a divisão do tiro ou a troca entre real e falso. */
    public Projectile(
        double x, double y, double aimX, double aimY, 
        boolean fromBoss, Color color, int radius) {
        this.x = x;
        this.y = y;
        vx = aimX * 3;
        vy = aimY * 3;
        this.fromBoss = fromBoss;
        this.color = color;
        this.radius = radius;
        splits = false;
        changesTruth = false;
    }

    /** Mantém o construtor com velocidade usado pelos jogadores e ataques simples. */
    public Projectile(double x, double y, double aimX, double aimY, double speed,
                      boolean fromBoss, Color color, int radius) {
        this(x, y, aimX, aimY, speed, fromBoss, color, radius, false, false);
    }

    /** Cria um projétil com velocidade e comportamentos opcionais dos chefes. */
    public Projectile(double x, double y, double aimX, double aimY, double speed,
                      boolean fromBoss, Color color, int radius,
                      boolean splits, boolean changesTruth) {
        this.x = x;
        this.y = y;
        double dx = aimX - x;
        double dy = aimY - y;
        double length = Math.hypot(dx, dy);
        if (length > 0) {
            vx = dx / length * speed;
            vy = dy / length * speed;
        }
        this.fromBoss = fromBoss;
        this.color = color;
        this.radius = radius;
        this.splits = splits;
        this.changesTruth = changesTruth;
    }

    /** Avança o projétil um quadro. */
    public void update() {
        x += vx;
        y += vy;
        frames++;
        if (splits && frames >= 60) {
            splitReady = true;
        }
        if (changesTruth && frames % 36 == 0) {
            harmful = !harmful;
        }
    }
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int)x - radius, (int)y - radius, radius * 2, radius * 2);
    }

    /** Diz se já saiu da tela para a lista poder removê-lo e poupar memória. */
    public boolean outside(int w, int h) { return x < -20 || x > w + 20 || y < 30 || y > h + 20; }

    public boolean getFromBoss() { return fromBoss; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean fromBoss() { return fromBoss; }
    public double x() { return x; }
    public double y() { return y; }
    public boolean consumeSplitReady() {
        if (!splitReady) return false;
        splitReady = false;
        return true;
    }
    public boolean isHarmful() { return harmful; }
}
