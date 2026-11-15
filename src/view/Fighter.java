package view;

import model.Player;
import model.Projectile;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

class Fighter {
    static final int MAX_LIVES = 5;
    static final int MAX_SHIELDS = 3;
    private static final int HIT_INVULNERABILITY_FRAMES = 48;
    private static final int SHIELD_RECHARGE_DELAY = 150;
    private static final int SHIELD_RECHARGE_INTERVAL = 120;
    private static final int PLAYER_SIZE = 22;
    private static final double MAX_STAMINA = 100;
    private static final double SHOT_STAMINA_COST = 4;

    private final Player character;
    private final int startX;
    private final int startY;
    private int lives = MAX_LIVES;
    private int shields = MAX_SHIELDS;
    private int invulnerabilityFrames;
    private int shieldRechargeFrames;
    private int shieldRechargePeriod;
    private double stamina = MAX_STAMINA;
    private double aimX;
    private double aimY = -1;
    private boolean rechargingStamina;

    /** Cria o personagem visual e guarda sua posição inicial para reinícios. */
    Fighter(int x, int y, Color color) {
        character = new Player(x, y, color);
        startX = x;
        startY = y;
    }

    /**
     * Chamado a cada quadro: reduz invulnerabilidade e delega recargas de escudo e stamina
     * aos métodos responsáveis por cada contador.
     */
    void update() {
        if (invulnerabilityFrames > 0) {
            invulnerabilityFrames--;
        }
        updateShieldRecharge();
        updateStaminaRecharge();
    }

    /** Recarrega um escudo por vez após o atraso configurado desde o último golpe. */
    private void updateShieldRecharge() {
        if (isDead() || shields == MAX_SHIELDS || shieldRechargeFrames <= 0) {
            return;
        }

        shieldRechargeFrames--;
        if (shieldRechargeFrames == 0) {
            shields++;
            if (shields < MAX_SHIELDS) {
                shieldRechargePeriod = SHIELD_RECHARGE_INTERVAL;
                shieldRechargeFrames = SHIELD_RECHARGE_INTERVAL;
            } else {
                shieldRechargePeriod = 0;
            }
        }
    }

    /** Recupera stamina gradualmente após o jogador esgotar a barra ao atirar. */
    private void updateStaminaRecharge() {
        if (!rechargingStamina) {
            return;
        }
        stamina = Math.min(MAX_STAMINA, stamina + 0.65);
        if (stamina >= MAX_STAMINA) {
            rechargingStamina = false;
        }
    }

    /** Normaliza a direção, atualiza a mira e move o personagem dentro dos limites. */
    void move(double dx, double dy, int speed,
              int minX, int minY, int maxX, int maxY) {
        if (!isAlive() || (dx == 0 && dy == 0)) {
            return;
        }
        double length = Math.hypot(dx, dy);
        aimX = dx / length;
        aimY = dy / length;
        character.move(aimX * speed, aimY * speed, minX, minY, maxX, maxY);
    }

    /** Consome stamina e cria um tiro na direção da última mira; retorna null se indisponível. */
    Projectile fire() {
        if (!isAlive() || rechargingStamina || stamina < SHOT_STAMINA_COST) {
            return null;
        }

        stamina -= SHOT_STAMINA_COST;
        if (stamina <= 0) {
            stamina = 0;
            rechargingStamina = true;
        }
        return new Projectile(centerX(), centerY(),
                centerX() + aimX * 500, centerY() + aimY * 500,
                8.5, false, color(), 5);
    }

    /** Reduz escudo ou vida, inicia invulnerabilidade e informa se esse golpe foi fatal. */
    boolean receiveHit(boolean shieldPiercing) {
        if (!canReceiveHit()) {
            return false;
        }

        if (shields > 0 && !shieldPiercing) {
            shields--;
        } else {
            lives--;
        }
        invulnerabilityFrames = HIT_INVULNERABILITY_FRAMES;
        if (shields < MAX_SHIELDS) {
            shieldRechargePeriod = SHIELD_RECHARGE_DELAY;
            shieldRechargeFrames = SHIELD_RECHARGE_DELAY;
        }
        return isDead();
    }

    /** Impede dano quando o jogador morreu ou ainda está invulnerável. */
    boolean canReceiveHit() {
        return isAlive() && invulnerabilityFrames == 0;
    }

    /** Reposiciona o jogador e restaura vidas, escudos, mira e stamina. */
    void reset() {
        double currentX = centerX() - PLAYER_SIZE / 2.0;
        double currentY = centerY() - PLAYER_SIZE / 2.0;
        character.move(startX - currentX, startY - currentY,
                GamePanel.ARENA_LEFT, GamePanel.ARENA_TOP,
                GamePanel.ARENA_RIGHT, GamePanel.ARENA_BOTTOM);
        lives = MAX_LIVES;
        shields = MAX_SHIELDS;
        invulnerabilityFrames = 0;
        shieldRechargeFrames = 0;
        shieldRechargePeriod = 0;
        stamina = MAX_STAMINA;
        aimX = 0;
        aimY = -1;
        rechargingStamina = false;
    }

    /** Desenha escudo e personagem, piscando enquanto o jogador está invulnerável. */
    void draw(Graphics2D g) {
        if (isDead() || (invulnerabilityFrames > 0 && (invulnerabilityFrames / 4) % 2 == 0)) {
            return;
        }

        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();
        if (shields > 0) {
            int alpha = 65 + shields * 45;
            g.setColor(new Color(80, 220, 255, alpha));
            g.setStroke(new BasicStroke(2.0f));
            g.drawOval((int) centerX() - 17, (int) centerY() - 17, 34, 34);
        }
        g.setStroke(originalStroke);
        g.setColor(originalColor);
        character.draw(g);
    }

    /** Indica se ainda há pelo menos uma vida. */
    boolean isAlive() { return lives > 0; }
    /** Indica se as vidas chegaram a zero ou menos. */
    boolean isDead() { return lives <= 0; }
    /** Devolve vidas atuais para colisões e HUD. */
    int lives() { return lives; }
    /** Devolve escudos atuais para colisões e HUD. */
    int shields() { return shields; }
    /** Devolve stamina atual para o indicador inferior. */
    double stamina() { return stamina; }
    /** Devolve a cor do objeto Player para desenho e explosões. */
    Color color() { return character.getColor(); }
    /** Devolve a coordenada horizontal central do personagem. */
    double centerX() { return character.centerX(); }
    /** Devolve a coordenada vertical central do personagem. */
    double centerY() { return character.centerY(); }

    /** Calcula a fração de recarga do próximo escudo para preencher o indicador do HUD. */
    double shieldRegenerationProgress() {
        if (shields == MAX_SHIELDS || shieldRechargePeriod == 0) {
            return 0;
        }
        return Math.max(0, Math.min(1,
                1.0 - (double) shieldRechargeFrames / shieldRechargePeriod));
    }
}
