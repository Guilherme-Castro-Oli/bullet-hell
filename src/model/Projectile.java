package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Um projétil guarda posição, direção e velocidade.
 * O campo fromBoss permite que a tela saiba se o tiro deve ferir um jogador
 * ou o chefe, sem precisar de duas classes de projétil diferentes.
 */
public class Projectile {
    // vx e vy são quanto o projétil avança em cada atualização do jogo.
    protected double x, y, vx, vy;
    protected  final boolean fromBoss;
    protected final Color color;
    protected final int radius;
    protected int frames;

    //projetil do jogador
    public Projectile(
        double x, double y, double aimX, double aimY, 
        boolean fromBoss, Color color, int radius) {
        this.x = x;
        this.y = y;
        vx = aimX * 3;
        vy = aimY * 3;
        if (fromBoss){
            double dx = aimX - x;
            double dy = aimY - y;
            double length = Math.hypot(dx, dy);
            if (length > 0) {
                vx = dx / length * 3;
                vy = dy / length * 3;
            }
        }
        this.fromBoss = fromBoss;
        this.color = color;
        this.radius = radius;
    }

    /** Avança o projétil um quadro. */
    public void update(ArrayList<Projectile> projectiles) {
        x += vx;
        y += vy;
        /*
        
        if (changesTruth && frames % 36 == 0) {
            harmful = !harmful;
        }
            */
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
    public int getRadius() { return radius; }
}
