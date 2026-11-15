package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class Portal {
    private static final int DRAW_HEIGHT = 108;
    private static final double INTERACTION_RADIUS = 62;

    private final int centerX;
    private final int centerY;
    private final BufferedImage image;
    private final Rectangle visibleBounds;
    private int animationTick;

    /** Guarda centro e imagem e calcula uma vez a área visível da imagem transparente. */
    Portal(int centerX, int centerY, BufferedImage image) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.image = image;
        visibleBounds = findVisibleBounds(image);
    }

    /** Avança o relógio que controla a pulsação visual do portal. */
    void update() {
        animationTick++;
    }

    /** Verifica se um jogador vivo está dentro do raio de interação do portal. */
    boolean isPlayerNear(Fighter fighter) {
        return fighter.isAlive()
                && Math.hypot(fighter.centerX() - centerX, fighter.centerY() - centerY)
                <= INTERACTION_RADIUS;
    }

    /** Retorna true se pelo menos um jogador puder alcançar o portal. */
    boolean isPlayerNear(Fighter first, Fighter second) {
        return isPlayerNear(first) || isPlayerNear(second);
    }

    /** Desenha o brilho pulsante e a imagem do portal, com forma vetorial de reserva. */
    void draw(Graphics2D g, boolean playerNearby) {
        float pulse = (float) ((Math.sin(animationTick * 0.09) + 1) / 2);
        int alpha = playerNearby ? 55 + (int) (75 * pulse) : 24 + (int) (40 * pulse);
        int glowWidth = playerNearby ? 92 : 78;
        g.setColor(new Color(40, 160, 255, alpha));
        g.fillOval(centerX - glowWidth / 2, centerY + 39, glowWidth,
                playerNearby ? 26 : 20);

        if (image == null || visibleBounds == null) {
            g.setColor(new Color(35, 145, 255, 180));
            g.fillOval(centerX - 30, centerY - 50, 60, 100);
            g.setColor(new Color(125, 235, 255));
            g.drawOval(centerX - 30, centerY - 50, 60, 100);
            return;
        }

        int drawWidth = Math.max(1, (int) Math.round(
                visibleBounds.width * DRAW_HEIGHT / (double) visibleBounds.height));
        int left = centerX - drawWidth / 2;
        int top = centerY - DRAW_HEIGHT / 2;
        g.drawImage(image, left, top, left + drawWidth, top + DRAW_HEIGHT,
                visibleBounds.x, visibleBounds.y,
                visibleBounds.x + visibleBounds.width,
                visibleBounds.y + visibleBounds.height, null);
    }

    /** Encontra a área não transparente para remover margens vazias ao desenhar o portal. */
    /**
     * Varre os pixels da imagem e encontra o retângulo cujo alpha é maior que 8.
     * Portal.draw usa essa área para dimensionar o desenho sem margens transparentes.
     */
    private Rectangle findVisibleBounds(BufferedImage source) {
        if (source == null) {
            return null;
        }
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if (((source.getRGB(x, y) >>> 24) & 0xFF) > 8) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
