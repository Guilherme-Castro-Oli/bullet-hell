package view;

import model.Boss;
import java.awt.Color;

class HerancaBoss extends Boss implements BossBehavior {
    private int attackFrame;
    private int volleyIndex;

    /** Inicializa identidade, cor e total de vida do primeiro chefe. */
    HerancaBoss() {
        super("HERANÇA", "O Descendente", new Color(70, 190, 100), 18);
    }

    /** Alterna os padrões de ataque após cada intervalo de 105 quadros. */
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
}
