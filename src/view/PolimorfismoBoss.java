package view;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Chefe que alterna a própria forma visual e seus ataques ao longo do tempo.
 * Um relógio local é consultado tanto pela animação quanto pelo comportamento
 * de ataque, para que as mudanças permaneçam sincronizadas com os quadros que
 * GamePanel processa e encaminha ao chefe.
 */
class PolimorfismoBoss extends GameBoss implements BossBehavior {
    /** Período entre rajadas em leque, expresso em quadros do temporizador do jogo. */
    private static final int VOLLEY_INTERVAL = 30;
    /** Relógio local consultado pelo desenho e pela lógica de ataques periódicos. */
    private int frame;

    /** Inicializa o chefe centralizado e seu relógio de ataques. */
    PolimorfismoBoss() {
        super("POLIMORFISMO", "O Mutante", new Color(230, 75, 75), 23);
    }

    /** Avança o relógio próprio sem alterar a posição fixa no centro da arena. */
    @Override
    public void update(int width) {
        frame++;
    }

    /** Devolve o relógio local usado para sincronizar animação e rajadas. */
    @Override
    public int tick() {
        return frame;
    }

    /** Fixa a coordenada horizontal do chefe no centro da arena. */
    @Override
    public double x() {
        return GamePanel.ARENA_CENTER_X;
    }

    /** Fixa a coordenada vertical do chefe no centro da arena. */
    @Override
    public double y() {
        return GamePanel.ARENA_CENTER_Y;
    }

    /** Desenha a forma animada, o nome e a barra de vida do chefe. */
    @Override
    public void draw(Graphics2D g) {
        int radius = 31;
        boolean spriteDrawn = BossSprites.draw(g, BossSprites.Type.POLIMORFISMO,
                x(), y(), frame, 112);
        if (!spriteDrawn) {
            // Forma geométrica de reserva para execução sem a folha de sprites.
            g.setColor(new Color(230, 75, 75));
            int shape = (frame / 12) % 3;
            if (shape == 0) {
                g.fillOval((int) x() - radius, (int) y() - radius,
                        radius * 2, radius * 2);
            } else if (shape == 1) {
                int[] xPoints = {(int) x(), (int) x() + radius, (int) x(), (int) x() - radius};
                int[] yPoints = {(int) y() - radius, (int) y(), (int) y() + radius, (int) y()};
                g.fillPolygon(xPoints, yPoints, 4);
            } else {
                g.fillRoundRect((int) x() - radius, (int) y() - radius,
                        radius * 2, radius * 2, 12, 12);
            }
        }

        g.setColor(Color.WHITE);
        String label = concept();
        g.drawString(label, (int) x() - g.getFontMetrics().stringWidth(label) / 2,
                (int) y() - 64);
        g.setColor(new Color(50, 20, 20));
        g.fillRect((int) x() - 40, (int) y() + 56, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect((int) x() - 40, (int) y() + 56, 80 * hp() / maxHp(), 7);
    }

    /** Dispara o leque periódico e uma rajada radial menos frequente. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        // A velocidade aumenta quando a vida cai; os dois padrões reutilizam esse valor.
        double shotSpeed = (double) hp() / maxHp() <= 0.75 ? 2.8 : 2.4;
        if (frame % VOLLEY_INTERVAL == 0) {
            game.shootFan(this, target, 7, 1.15, false, true,
                    shotSpeed, new Color(255, 75, 100));
        }
        if (frame > 0 && frame % (VOLLEY_INTERVAL * 4) == 0) {
            game.shootRadial(this, 10, shotSpeed - 0.2,
                    new Color(190, 75, 255), true);
        }
    }
}
