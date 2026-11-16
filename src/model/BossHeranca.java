package model;

import java.awt.Color;
import java.awt.Graphics2D;

public class BossHeranca extends Boss {
    private int moveTick = 0;

    /** Inicializa chefe em alguma posição. */
    public BossHeranca(double x, double y) {
        super("O Descendente", new Color(70, 190, 100), 10, x, y);
    }

    public void draw(Graphics2D g){
        if (dead) {
            return;
        }
        g.setColor(color);
        g.fillRect((int)x-radius, (int)y-radius, radius*2, radius*2);
        g.setColor(Color.WHITE);
        // O título mostra somente o conceito de POO, conforme o nome do chefe.
        g.drawString(name, (int)x - g.getFontMetrics().stringWidth(name) / 2, (int)y - 40);
        g.setColor(new Color(50, 20, 20)); 
        g.fillRect((int)x-40, (int)y+38, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect((int)x-40, (int)y+38, 80 * hp / maxHP, 7);
    }

    //coisa pra movimento do heranca
    public void move(int width){
        if (dead) {
            return;
        }
        moveTick++;
        // O chefe se desloca lateralmente enquanto ataca. Seno cria movimento
        // contínuo de ida e volta; tick funciona como relógio em quadros.
        x = width / 2.0 + Math.sin(moveTick * 0.025) * Math.min(180, width * 0.25);
        if (moveTick > 250){
            moveTick = 0;
        }
    }

    public Projectile shoot(Player player) {
        return new Proj_split(
            x, y, player.centerX(), player.centerY(), true, 
            new Color(244,131,38), 5);

    }
}
