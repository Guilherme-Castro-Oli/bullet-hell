package view;

import java.awt.Color;

/**
 * Projétil da arena atual, com velocidade, divisão e alternância de perigo próprias.
 * Herda de model.Projectile para reutilizar o desenho pixelado já existente no projeto;
 * esta classe acrescenta somente os estados e regras especiais usados pela arena atual.
 */
class ArenaProjectile extends model.Projectile {
    /** Divide o tiro antes que a trajetória padrão alcance a posição inicial dos jogadores. */
    private static final int SPLIT_AFTER_FRAMES = 36;
    private static final int TRUTH_CHANGE_INTERVAL = 36;
    /** Cor de leitura dos projéteis do segundo chefe enquanto não causam dano. */
    private static final Color SAFE_COLOR = new Color(95, 205, 255);

    /** Configurações especiais dos tiros de chefe, consumidas pelos contadores de quadro. */
    private boolean splits;
    private final boolean changesTruth;
    private boolean harmful = true;
    private boolean splitReady;
    private int frames;

    /** Cria um projétil comum, sem divisão nem alternância periódica de perigo. */
    ArenaProjectile(double x, double y, double aimX, double aimY, double speed,
                    boolean fromBoss, Color color, int radius) {
        this(x, y, aimX, aimY, speed, fromBoss, color, radius, false, false);
    }

    /** Cria um projétil e configura os comportamentos especiais opcionais dos chefes. */
    ArenaProjectile(double x, double y, double aimX, double aimY, double speed,
                    boolean fromBoss, Color color, int radius,
                    boolean splits, boolean changesTruth) {
        super(x, y, aimX, aimY, fromBoss, color, radius);
        this.splits = splits;
        this.changesTruth = changesTruth;

        // Aim é um ponto de destino, portanto a direção precisa ser normalizada antes da velocidade.
        double dx = aimX - x;
        double dy = aimY - y;
        double distance = Math.hypot(dx, dy);
        if (distance == 0) {
            vx = 0;
            vy = 0;
        } else {
            vx = dx / distance * speed;
            vy = dy / distance * speed;
        }
    }

    /** Avança uma posição, conta um quadro e atualiza os estados de divisão e alternância. */
    void update() {
        x += vx;
        y += vy;
        frames++;

        if (splits && frames >= SPLIT_AFTER_FRAMES) {
            splitReady = true;
            splits = false;
        }
        if (changesTruth && frames % TRUTH_CHANGE_INTERVAL == 0) {
            harmful = !harmful;
        }
    }

    /** Devolve e limpa o aviso de divisão para que ele gere novos tiros somente uma vez. */
    boolean consumeSplitReady() {
        boolean ready = splitReady;
        splitReady = false;
        return ready;
    }

    /** Devolve se o tiro foi criado pelo chefe ou por um jogador. */
    boolean fromBoss() {
        return fromBoss;
    }

    /** Devolve se o estado atual deste projétil permite causar dano. */
    boolean isHarmful() {
        return harmful;
    }

    /** Usa a mesma arte-base e só troca a paleta quando o projétil alterna seu perigo. */
    @Override
    protected Color displayColor() {
        return changesTruth && !harmful ? SAFE_COLOR : color;
    }

    /** Posição horizontal atual, compartilhada com colisões e criação de estilhaços. */
    double x() {
        return x;
    }

    /** Posição vertical atual, compartilhada com colisões e criação de estilhaços. */
    double y() {
        return y;
    }
}
