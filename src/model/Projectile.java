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
        if (fromBoss) {
            drawPixelOrb(g);
            return;
        }
        drawPixelShot(g);
    }

    /**
     * Fornece a cor-base do desenho e permite que projéteis especializados mudem
     * sua aparência sem duplicar os formatos pixelados usados por esta classe.
     */
    protected Color displayColor() {
        return color;
    }

    /** Desenha o disparo do jogador como um raio pixelado apontado para seu movimento. */
    private void drawPixelShot(Graphics2D g) {
        String[] pixels = {
                ".....111...",
                "....1221...",
                "..1123321..",
                ".1233443321",
                "12334444321",
                ".1233443321",
                "..1123321..",
                "....1221...",
                ".....111..."
        };
        Color shotColor = displayColor();
        Color[] palette = {
                null,
                shotColor.darker().darker(),
                shotColor,
                shade(shotColor, 0.48),
                shade(shotColor, 0.86)
        };
        int pixelSize = 2;
        int width = pixels[0].length() * pixelSize;
        int height = pixels.length * pixelSize;
        Graphics2D shot = (Graphics2D) g.create();
        shot.translate(x, y);
        shot.rotate(Math.atan2(vy, vx));
        for (int row = 0; row < pixels.length; row++) {
            for (int column = 0; column < pixels[row].length(); column++) {
                int shade = pixels[row].charAt(column) - '0';
                if (shade <= 0) continue;
                shot.setColor(palette[shade]);
                shot.fillRect(-width / 2 + column * pixelSize,
                        -height / 2 + row * pixelSize, pixelSize, pixelSize);
            }
        }
        shot.dispose();
    }

    /** Desenha os tiros dos chefes como o mesmo orbe pixelado, tingido pela cor do chefe. */
    private void drawPixelOrb(Graphics2D g) {
        String[] pixels = {
                "...111...",
                "..12221..",
                ".1223321.",
                "12233321.",
                "123344321",
                "123444321",
                ".1233321.",
                "..12221..",
                "...111..."
        };
        Color shotColor = displayColor();
        Color[] palette = {
                null,
                shotColor.darker().darker(),
                shotColor,
                shade(shotColor, 0.48),
                shade(shotColor, 0.82)
        };
        int pixelSize = 2;
        int left = (int) Math.round(x) - pixels[0].length() * pixelSize / 2;
        int top = (int) Math.round(y) - pixels.length * pixelSize / 2;
        for (int row = 0; row < pixels.length; row++) {
            for (int column = 0; column < pixels[row].length(); column++) {
                int shade = pixels[row].charAt(column) - '0';
                if (shade <= 0) continue;
                g.setColor(palette[shade]);
                g.fillRect(left + column * pixelSize, top + row * pixelSize,
                        pixelSize, pixelSize);
            }
        }
    }

    private Color shade(Color source, double amount) {
        int red = (int) (source.getRed() + (255 - source.getRed()) * amount);
        int green = (int) (source.getGreen() + (255 - source.getGreen()) * amount);
        int blue = (int) (source.getBlue() + (255 - source.getBlue()) * amount);
        return new Color(red, green, blue);
    }

    /** Diz se já saiu da tela para a lista poder removê-lo e poupar memória. */
    public boolean outside(int w, int h) { return x < -20 || x > w + 20 || y < 30 || y > h + 20; }

    public boolean getFromBoss() { return fromBoss; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getRadius() { return radius; }
}
