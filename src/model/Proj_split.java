package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Proj_split extends Projectile {

    /** Inicializa chefe em alguma posição. */
    public Proj_split(
        double x, double y, double aimX, double aimY, 
        boolean fromBoss, Color color, int radius) {
        super(x, y, aimX, aimY, fromBoss, color, radius * 2);
    }

    public void update(ArrayList<Projectile> projectiles) {
        x += vx;
        y += vy;
        frames++;
        if (frames >= 60) {
            frames = 0;
            for (int index = 0; index < 8; index++) {
                double angle = Math.PI * 2 * index / 8;
                projectiles.add(new Projectile(x, y,
                        x + Math.cos(angle) * 100,
                        y + Math.sin(angle) * 100,
                        fromBoss, color, radius / 2));
            }
            x = -100;
        }
    }
}
