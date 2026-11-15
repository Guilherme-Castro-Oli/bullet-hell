package view;

import model.Boss;
import java.awt.Color;

class AbstracaoBoss extends Boss implements BossBehavior {
    private int attackFrame;

    /** Inicializa identidade, aparência e vida do chefe final. */
    AbstracaoBoss() {
        super("ABSTRAÇÃO", "O Molde Antigo", new Color(235, 170, 55), 32);
    }

    /** Emite o leque a cada 25 quadros e a rajada radial a cada 50. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        attackFrame++;
        if (attackFrame % 25 == 0) {
            game.shootFan(this, target, 7, 1.05, false, false,
                    4.8, new Color(235, 170, 55));
        }
        if (attackFrame % 50 == 0) {
            game.shootRadial(this, 8, 4.25, new Color(255, 185, 70));
        }
    }
}
