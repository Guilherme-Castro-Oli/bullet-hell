package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;

/**
 * Representa um jogador e guarda os dados que pertencem somente a ele.
 * Esta classe não lê teclado nem mouse: a tela decide quando chamá-la.
 * Isso separa os dados/regras do personagem da interface gráfica.
 */
public class Player {
    // double permite que o movimento use frações de pixel e fique mais suave.
    private double x, y;
    // final significa que cor, tamanho e nome são definidos na criação do jogador.
    private final Color color;
    private final int size = 22;
    private final int maxShield = 3;
    public int health = 5;
    private int shields = maxShield;
    private double aimX, aimY;
    private int cooldown = 0;
    private boolean dead = false;
    private int shieldRechargeFrames = 0;
    private int shieldRechargePeriod = 0;

    /** Cria um jogador na posição indicada e com uma cor fácil de reconhecer. */
    public Player(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        aimY = -3;
    }

    /**
     * Soma o deslocamento pedido à posição atual.
     * width e height vêm da área de jogo; usamos size para impedir que
     * qualquer parte do quadrado ultrapasse suas bordas.
     */
    public void move(double dx, double dy, int minx, int miny, int maxx, int maxy) {
        aimX = dx; aimY = dy;
        // Math.min limita o máximo e Math.max limita o mínimo da coordenada.
        x = Math.max(minx, Math.min(maxx - size, x + dx));
        // A coordenada 40 deixa livre a faixa superior usada pelo HUD.
        y = Math.max(miny, Math.min(maxy - size, y + dy));
    }

    /** Desenha o quadrado preenchido e uma borda branca para destacá-lo. */
    public void draw(Graphics2D g) {
        if (health <= 0) {
            return;
        }
        g.setColor(color);
        g.fillRect((int)x, (int)y, size, size);
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y, size, size);
    }

    /** Consome stamina e cria um tiro na direção da última mira; retorna null se indisponível. */
    public Projectile fire() {
        return new Projectile(
            centerX(), centerY(), aimX, aimY, false, 
            new Color(244,131,38), 5, 1);
    }

    public boolean canShoot() {
        if (health <= 0) {
            return false;
        }
        cooldown++;
        if (cooldown > 10) {
            cooldown = 0;
            return true;
        }
        return false;
    }

    /**
     * Usa os dados do Fighter para desenhar vidas, escudos disponíveis/em recarga e a
     * barra de stamina. x/y definem a origem do bloco; label identifica P1 ou P2.
     */
    public void drawPlayerHud(Graphics2D g, int x, int y, String label) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(color);
        g.drawString(label, x, y);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(new Color(235, 238, 228));
        g.drawString("VIDA", x + 38, y);
        for (int index = 0; index < 5; index++) {
            int blockX = x + 72 + index * 15;
            g.setColor(index < health ? color : new Color(75, 82, 80));
            g.fillRoundRect(blockX, y - 10, 11, 9, 4, 4);
        }

        g.setColor(new Color(235, 238, 228));
        g.drawString("ESCUDO", x + 152, y);
        for (int index = 0; index < maxShield; index++) {
            int blockX = x + 200 + index * 14;
            g.setColor(new Color(45, 64, 67));
            g.fillRoundRect(blockX, y - 10, 10, 9, 4, 4);
            if (index < shields) {
                g.setColor(new Color(80, 220, 255));
                g.fillRoundRect(blockX, y - 10, 10, 9, 4, 4);
            } else if (index == shields) {
                int progress = (int) (10 * shieldRegenerationProgress());
                if (progress > 0) {
                    g.setColor(new Color(80, 220, 255, 190));
                    g.fillRoundRect(blockX, y - 10, progress, 9, 4, 4);
                }
            }
        }
    }

    public boolean hit(Projectile proj){
        if (!proj.getFromBoss() || dead) {
            return false;
        }
        if (proj.getX() < x + size && proj.getX() + proj.getRadius() > x &&
            proj.getY() < y + size && proj.getY() + proj.getRadius() > y) {
            
            if (shields <= 0) {
                health--;
            } else {
                if (shields == maxShield) {
                    shieldRechargePeriod = 120;
                    shieldRechargeFrames = 120;
                }
                shields--;
            }
            if (health <= 0) {
                dead = true;
            }
            return true;
        }
        return false;
    }

    /** Recarrega um escudo por vez após o atraso configurado desde o último golpe. */
    public void updateShieldRecharge() {
        if (isDead() || shields == maxShield || shieldRechargeFrames <= 0) {
            return;
        }

        shieldRechargeFrames--;
        if (shieldRechargeFrames == 0) {
            shields++;
            if (shields < maxShield) {
                shieldRechargePeriod = 120;
                shieldRechargeFrames = 120;
            } else {
                shieldRechargePeriod = 0;
            }
        }
    }

    /** Calcula a fração de recarga do próximo escudo para preencher o indicador do HUD. */
    private double shieldRegenerationProgress() {
        if (shields == maxShield || shieldRechargePeriod == 0) {
            return 0;
        }
        return Math.max(0, Math.min(1, 1.0 - (double) shieldRechargeFrames / shieldRechargePeriod));
        
    }

    // Os tiros partem do centro do quadrado, não de um de seus cantos.
    public double centerX() { return x + (size / 2.0); }
    public double centerY() { return y + (size / 2.0); }
    public Color getColor() { return color; }
    public boolean isDead() { return dead; }
}
