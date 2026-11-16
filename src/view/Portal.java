package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Representa o portal de transição entre chefes na tela principal do jogo.
 * Mantém sua posição e imagem próprias, calcula uma única vez a parte visível
 * da imagem e expõe operações pequenas para atualização, proximidade e desenho.
 * A classe não escolhe o jogador que o ativa nem troca a tela: essa decisão
 * continua sob responsabilidade de GamePanel.
 */
class Portal {
    /** Grade 4x4 da folha portal.png e tempo de permanência de cada quadro. */
    private static final int SHEET_COLUMNS = 4;
    private static final int SHEET_ROWS = 4;
    private static final int FRAME_DURATION = 7;
    /** Altura final usada ao desenhar o portal, preservando a proporção de cada quadro. */
    private static final int DRAW_HEIGHT = 108;
    /** Distância máxima entre centro do portal e jogador para considerá-lo próximo. */
    private static final double INTERACTION_RADIUS = 62;

    /** Coordenadas imutáveis do centro, compartilhadas pelo desenho e pela checagem de alcance. */
    private final int centerX;
    private final int centerY;
    /** Imagem opcional; null seleciona a forma vetorial de reserva. */
    private final BufferedImage image;
    /** Dimensões de uma célula da folha e recortes visíveis pré-calculados para os 16 quadros. */
    private final int frameWidth;
    private final int frameHeight;
    private final Rectangle[] visibleBounds;
    /** Relógio local que alimenta a pulsação do brilho sem depender de horário do sistema. */
    private int animationTick;

    /** Guarda centro e imagem e calcula uma vez a área visível da imagem transparente. */
    Portal(int centerX, int centerY, BufferedImage image) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.image = image;
        frameWidth = image == null ? 0 : image.getWidth() / SHEET_COLUMNS;
        frameHeight = image == null ? 0 : image.getHeight() / SHEET_ROWS;
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
        // A senoide converte o relógio em um pulso suave entre zero e um.
        float pulse = (float) ((Math.sin(animationTick * 0.09) + 1) / 2);
        int alpha = playerNearby ? 55 + (int) (75 * pulse) : 24 + (int) (40 * pulse);
        int glowWidth = playerNearby ? 92 : 78;
        g.setColor(new Color(40, 160, 255, alpha));
        g.fillOval(centerX - glowWidth / 2, centerY + 39, glowWidth,
                playerNearby ? 26 : 20);

        // Sem imagem ou sem qualquer pixel visível, o portal ainda pode ser representado.
        if (image == null || visibleBounds == null || visibleBounds.length == 0) {
            g.setColor(new Color(35, 145, 255, 180));
            g.fillOval(centerX - 30, centerY - 50, 60, 100);
            g.setColor(new Color(125, 235, 255));
            g.drawOval(centerX - 30, centerY - 50, 60, 100);
            return;
        }

        // A folha tem 16 quadros; trocar de célula anima a espiral sem redesenhar o sprite.
        int frame = (animationTick / FRAME_DURATION) % visibleBounds.length;
        Rectangle bounds = visibleBounds[frame];
        if (bounds == null || bounds.height == 0) {
            return;
        }
        int column = frame % SHEET_COLUMNS;
        int row = frame / SHEET_COLUMNS;
        int sourceX = column * frameWidth + bounds.x;
        int sourceY = row * frameHeight + bounds.y;
        int drawWidth = Math.max(1, (int) Math.round(
                bounds.width * DRAW_HEIGHT / (double) bounds.height));
        int left = centerX - drawWidth / 2;
        int top = centerY - DRAW_HEIGHT / 2;
        Graphics2D spriteGraphics = (Graphics2D) g.create();
        spriteGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        spriteGraphics.drawImage(image, left, top, left + drawWidth, top + DRAW_HEIGHT,
                sourceX, sourceY, sourceX + bounds.width, sourceY + bounds.height, null);
        spriteGraphics.dispose();
    }

    /**
     * Divide a folha em 16 células e encontra a área visível de cada quadro.
     * Uma pequena borda é ignorada para não incluir as linhas de separação da grade;
     * pixels quase transparentes também são descartados. As regiões são armazenadas
     * uma vez no construtor e depois reutilizadas por draw em todos os quadros do jogo.
     */
    private Rectangle[] findVisibleBounds(BufferedImage source) {
        if (source == null) {
            return null;
        }

        Rectangle[] frames = new Rectangle[SHEET_COLUMNS * SHEET_ROWS];
        for (int row = 0; row < SHEET_ROWS; row++) {
            for (int column = 0; column < SHEET_COLUMNS; column++) {
                int minX = frameWidth;
                int minY = frameHeight;
                int maxX = -1;
                int maxY = -1;
                int cellLeft = column * frameWidth;
                int cellTop = row * frameHeight;

                for (int y = 4; y < frameHeight - 4; y++) {
                    for (int x = 4; x < frameWidth - 4; x++) {
                        int alpha = (source.getRGB(cellLeft + x, cellTop + y) >>> 24) & 0xFF;
                        if (alpha > 8) {
                            minX = Math.min(minX, x);
                            minY = Math.min(minY, y);
                            maxX = Math.max(maxX, x);
                            maxY = Math.max(maxY, y);
                        }
                    }
                }

                if (maxX >= minX && maxY >= minY) {
                    frames[row * SHEET_COLUMNS + column] = new Rectangle(
                            minX, minY, maxX - minX + 1, maxY - minY + 1);
                }
            }
        }
        return frames;
    }
}
