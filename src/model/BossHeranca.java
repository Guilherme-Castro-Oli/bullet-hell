package model;

import java.awt.Color;
import java.awt.Graphics2D;

public class BossHeranca extends Boss {
    private int attackFrame;
    private int volleyIndex;

    /** Inicializa chefe em alguma posição. */
    public BossHeranca(double x, double y) {
        super("O Descendente", new Color(70, 190, 100), 20, x, y);
    }

    public void draw(Graphics2D g){
      int radius = 31;
        g.setColor(color);
        g.fillRect((int)x-radius, (int)y-radius, radius*2, radius*2);
        g.setColor(Color.WHITE);
        // O título mostra somente o conceito de POO, conforme o nome do chefe.
        g.drawString(name, (int)x - g.getFontMetrics().stringWidth(name) / 2, (int)y - 40);
        g.setColor(new Color(50, 20, 20)); g.fillRect((int)x-40, (int)y+38, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect((int)x-40, (int)y+38, 80 * hp / maxHP, 7);
    }

    public void move(int uh){

    }
    

    /** Alterna os padrões de ataque após cada intervalo de 105 quadros. */
    /*
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        if (++attackFrame < 105) {
            return;
        }
        attackFrame = 0;
        volleyIndex++;
        if (volleyIndex % 2 == 1) {
            game.shootFan(this, target, 1, 0, true, false,
                    2.45, new Color(95, 220, 120));
        } else {
            game.shootFan(this, target, 3, 0.52, false, false,
                    2.65, new Color(95, 220, 120));
        }
    }
    */
}
