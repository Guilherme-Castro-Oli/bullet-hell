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

    /** Opções extras ativam a divisão do tiro ou a troca entre real e falso. */
    public Projectile(
        double x, double y, double aimX, double aimY, 
        boolean fromBoss, Color color, int radius) {
        this.x = x; this.y = y; vx = aimX; vy = aimY;
        this.fromBoss = fromBoss; this.color = color; this.radius = radius;
    }

    /** Avança o projétil um quadro. */
    public void update() {
        x += vx * 3; y += vy * 3;
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
}
