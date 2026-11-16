package view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Carrega as folhas de sprites dos quatro chefes e desenha um quadro centralizado.
 * Cada folha informa sua própria grade, para que as imagens possam ser animadas sem
 * espalhar números de corte ou caminhos de arquivo pelas classes de comportamento.
 * Os limites transparentes de cada quadro são calculados durante a carga e reutilizados
 * a cada pintura, evitando varrer os pixels repetidamente no ciclo de renderização.
 */
final class BossSprites {
    /** Identifica o arquivo e a grade visual correspondentes a cada chefe da partida. */
    enum Type {
        HERANCA("folha de sprite boss 1.png", 3, 1, 0),
        POLIMORFISMO("folha de sprite boss 2.png", 3, 1, 0),
        ENCAPSULAMENTO("folha de sprite boss 3.png", 4, 5, 0),
        ABSTRACAO("folha de sprite boss 4.png", 4, 4, 0);

        private final String fileName;
        private final int columns;
        private final int rows;
        private final int animationRow;

        Type(String fileName, int columns, int rows, int animationRow) {
            this.fileName = fileName;
            this.columns = columns;
            this.rows = rows;
            this.animationRow = animationRow;
        }
    }

    /** Folhas carregadas uma vez para a execução, indexadas pelo tipo de chefe. */
    private static final Map<Type, SpriteSheet> SHEETS = loadSheets();

    private BossSprites() {
        // Classe utilitária: a aplicação usa somente os métodos estáticos.
    }

    /**
     * Desenha um quadro da animação do chefe, ajustado dentro de um quadrado de tamanho
     * máximo definido pelo chamador. O centro permanece fixo mesmo quando cada quadro
     * tem proporções ou margens transparentes diferentes.
     *
     * @param graphics contexto de desenho que pertence ao painel chamador
     * @param type folha de sprites associada ao chefe ativo
     * @param centerX coordenada horizontal do centro visual
     * @param centerY coordenada vertical do centro visual
     * @param tick relógio do chefe, usado para avançar o quadro da animação
     * @param maximumSize maior largura ou altura permitida para o sprite
     * @return true quando a folha e o quadro foram desenhados; false quando o recurso não pôde ser carregado
     */
    static boolean draw(Graphics2D graphics, Type type, double centerX, double centerY,
                        int tick, int maximumSize) {
        SpriteSheet sheet = SHEETS.get(type);
        if (sheet == null || maximumSize <= 0) {
            return false;
        }

        int frameIndex = (tick / sheet.framesPerImage) % sheet.columns;
        int row = type.animationRow;
        Rectangle visible = sheet.visibleBounds[row][frameIndex];
        if (visible == null) {
            return false;
        }

        // A escala usa a maior dimensão visível e preserva a proporção do desenho original.
        double scale = maximumSize / (double) Math.max(visible.width, visible.height);
        int drawWidth = Math.max(1, (int) Math.round(visible.width * scale));
        int drawHeight = Math.max(1, (int) Math.round(visible.height * scale));
        int left = (int) Math.round(centerX - drawWidth / 2.0);
        int top = (int) Math.round(centerY - drawHeight / 2.0);

        // Um contexto derivado limita a configuração de interpolação a este sprite.
        Graphics2D spriteGraphics = (Graphics2D) graphics.create();
        spriteGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int sourceX = frameIndex * sheet.frameWidth + visible.x;
        int sourceY = row * sheet.frameHeight + visible.y;
        spriteGraphics.drawImage(sheet.image,
                left, top, left + drawWidth, top + drawHeight,
                sourceX, sourceY, sourceX + visible.width, sourceY + visible.height, null);
        spriteGraphics.dispose();
        return true;
    }

    /** Carrega as quatro folhas e pré-calcula a região opaca de cada célula da grade. */
    private static Map<Type, SpriteSheet> loadSheets() {
        Map<Type, SpriteSheet> sheets = new EnumMap<>(Type.class);
        for (Type type : Type.values()) {
            BufferedImage image = loadImage(type.fileName);
            if (image != null && image.getWidth() >= type.columns && image.getHeight() >= type.rows) {
                sheets.put(type, new SpriteSheet(image, type.columns, type.rows));
            } else {
                System.err.println("Não foi possível carregar a folha do chefe: " + type.fileName);
            }
        }
        return sheets;
    }

    /**
     * Tenta primeiro o caminho de recurso do classpath, usado em uma distribuição empacotada;
     * se os recursos não forem copiados para a pasta de classes, tenta a estrutura src/image.
     */
    private static BufferedImage loadImage(String fileName) {
        try (InputStream resource = BossSprites.class.getResourceAsStream("/image/" + fileName)) {
            if (resource != null) {
                return ImageIO.read(resource);
            }
        } catch (IOException ignored) {
            // O caminho de desenvolvimento abaixo ainda pode disponibilizar o mesmo arquivo.
        }

        try {
            return ImageIO.read(new File("src/image", fileName));
        } catch (IOException ignored) {
            return null;
        }
    }

    /** Guarda dimensões da grade e recortes opacos para desenhar sem reprocessar a folha. */
    private static final class SpriteSheet {
        private final BufferedImage image;
        private final int columns;
        private final int frameWidth;
        private final int frameHeight;
        private final int framesPerImage = 12;
        private final Rectangle[][] visibleBounds;

        private SpriteSheet(BufferedImage image, int columns, int rows) {
            this.image = image;
            this.columns = columns;
            this.frameWidth = image.getWidth() / columns;
            this.frameHeight = image.getHeight() / rows;
            this.visibleBounds = new Rectangle[rows][columns];

            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    visibleBounds[row][column] = findVisibleBounds(
                            column * frameWidth, row * frameHeight, frameWidth, frameHeight);
                }
            }
        }

        /** Encontra a menor área de pixels visíveis dentro de uma única célula da folha. */
        private Rectangle findVisibleBounds(int left, int top, int width, int height) {
            int minX = width;
            int minY = height;
            int maxX = -1;
            int maxY = -1;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int alpha = (image.getRGB(left + x, top + y) >>> 24) & 0xFF;
                    if (alpha > 8) {
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
}
