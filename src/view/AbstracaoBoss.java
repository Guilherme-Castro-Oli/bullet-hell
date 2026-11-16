package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Random;

/**
 * Chefe que representa o conceito de abstração por meio de três corpos visuais
 * tratados como partes de uma única entidade lógica. O corpo central é a origem
 * real: ele define a vida e recebe o dano contabilizado; os outros dois existem
 * para executar comportamentos de perseguição, deslocamento e ataque distintos.
 * Esta classe concentra a regra específica do chefe, enquanto GamePanel continua
 * responsável por inserir projéteis, verificar impactos e desenhar o restante da arena.
 */
class AbstracaoBoss extends GameBoss implements BossBehavior {
    /** Quantidade fixa de corpos exibidos e atualizados como clones do mesmo chefe. */
    private static final int CLONE_COUNT = 3;
    /** Intervalo entre poderes especiais; a 16 ms por quadro corresponde a cerca de 17 s. */
    private static final int SPECIAL_POWER_FRAMES = 1_050; // 16,8 s a 16 ms por quadro
    /** Intervalo que define a troca de origem das rajadas de faixa. */
    private static final int POSITION_CHANGE_FRAMES = 92;
    /** Alcance do golpe corpo a corpo iniciado quando um jogador permanece parado. */
    private static final int MELEE_RADIUS = 158;
    /** Aviso visual mais curto antes de aplicar o golpe corpo a corpo. */
    private static final int WINDUP_FRAMES = 18;
    /** Cor compartilhada pelo corpo dos três clones. */
    private static final Color BODY_COLOR = new Color(235, 170, 55);

    /** Posições dos clones; o índice zero é o original e os índices um e dois são cópias. */
    private final double[] cloneX = new double[CLONE_COUNT];
    private final double[] cloneY = new double[CLONE_COUNT];
    /** Fonte aleatória usada somente para escolher o próximo destino do clone errante. */
    private final Random random = new Random();
    /** Relógio geral de animação e ataque, incrementado uma vez por atualização do jogo. */
    private int attackFrame;
    /** Destino temporário que o clone errante persegue até a próxima escolha aleatória. */
    private double wanderX = GamePanel.ARENA_CENTER_X + 125;
    private double wanderY = GamePanel.ARENA_TOP + 190;
    /** Quadros restantes até permitir outro golpe corpo a corpo. */
    private int meleeCooldown;
    /** Quadros restantes no aviso visual do golpe que está sendo preparado. */
    private int meleeWindup;
    /** Evita exibir a vida antes de o clone original receber o primeiro acerto. */
    private boolean healthRevealed;

    /** Cria o chefe e posiciona os três clones na formação inicial da arena. */
    AbstracaoBoss() {
        super("ABSTRAÇÃO", "O Molde Antigo", BODY_COLOR, 56);
        cloneX[0] = GamePanel.ARENA_CENTER_X;
        cloneY[0] = GamePanel.ARENA_TOP + 100;
        cloneX[1] = GamePanel.ARENA_CENTER_X - 125;
        cloneY[1] = GamePanel.ARENA_TOP + 165;
        cloneX[2] = GamePanel.ARENA_CENTER_X + 125;
        cloneY[2] = GamePanel.ARENA_TOP + 165;
    }

    /**
     * Informa se as coordenadas do projétil coincidem com algum dos três corpos.
     * Acertar uma cópia visual consome o projétil, mas apenas o original recebe dano.
     */
    boolean isHitAt(double x, double y) {
        for (int i = 0; i < CLONE_COUNT; i++) {
            if (Math.hypot(x - cloneX[i], y - cloneY[i]) < 35) return true;
        }
        return false;
    }

    /** Distingue um acerto no original de um acerto visual em uma cópia falsa. */
    boolean isOriginalHitAt(double x, double y) {
        return Math.hypot(x - cloneX[0], y - cloneY[0]) < 35;
    }

    /** Devolve a coordenada horizontal do corpo original, usada como posição lógica do chefe. */
    @Override
    public double x() { return cloneX[0]; }

    /** Devolve a coordenada vertical do corpo original, usada como posição lógica do chefe. */
    @Override
    public double y() { return cloneY[0]; }

    /** A barra fica oculta até o chefe original receber o primeiro acerto. */
    @Override
    public void damage() {
        healthRevealed = true;
        super.damage();
    }

    /** Desenha os três corpos com o mesmo molde; a barra de vida pertence à origem. */
    @Override
    public void draw(Graphics2D g) {
        Stroke previousStroke = g.getStroke();
        for (int i = 0; i < CLONE_COUNT; i++) {
            int x = (int) cloneX[i];
            int y = (int) cloneY[i];
            if (!BossSprites.draw(g, BossSprites.Type.ABSTRACAO, x, y, attackFrame, 100)) {
                // Forma de reserva compartilhada pelos clones caso a folha não esteja disponível.
                g.setColor(BODY_COLOR);
                g.fillRoundRect(x - 31, y - 31, 62, 62, 12, 12);
                g.setColor(new Color(255, 225, 150));
                g.setStroke(new BasicStroke(2.0f));
                g.drawRoundRect(x - 31, y - 31, 62, 62, 12, 12);
            }
            g.setColor(Color.WHITE);
            String label = concept();
            g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2, y - 58);
        }

        if (meleeWindup > 0) {
            float progress = 1.0f - (float) meleeWindup / WINDUP_FRAMES;
            int radius = (int) (MELEE_RADIUS * (0.55 + 0.45 * progress));
            int alpha = 105 + (int) (110 * progress);
            EncapsulamentoBoss.drawPixelRing(g, (int) cloneX[1], (int) cloneY[1],
                    radius, 5, new Color(35, 85, 145, alpha), 10);
            EncapsulamentoBoss.drawPixelRing(g, (int) cloneX[1], (int) cloneY[1],
                    radius - 9, 4, new Color(115, 195, 255, alpha), 5);
        }

        if (healthRevealed) {
            int originalX = (int) cloneX[0];
            int originalY = (int) cloneY[0];
            g.setColor(new Color(50, 20, 20));
            g.fillRect(originalX - 40, originalY + 50, 80, 7);
            g.setColor(new Color(255, 220, 70));
            g.fillRect(originalX - 40, originalY + 50, 80 * hp() / maxHp(), 7);
        }
        g.setStroke(previousStroke);
    }

    /** As cópias se movem e atacam de formas diferentes; a cada 17 s projetam uma grade. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        attackFrame++;
        double phase = attackFrame * 0.018;
        Fighter stationaryPlayer = game.stationaryTarget();
        Fighter chaseTarget = stationaryPlayer == null ? target : stationaryPlayer;

        // O chefe original percorre a arena em um arco amplo, em vez de ficar no alto.
        cloneX[0] = GamePanel.ARENA_CENTER_X + Math.sin(phase * 0.72) * 205;
        cloneY[0] = GamePanel.ARENA_TOP + 140 + Math.sin(phase * 1.08) * 62;

        // O falso segue o jogador escolhido como alvo.
        moveToward(1, chaseTarget.centerX(), chaseTarget.centerY(), 2.4);

        // O terceiro clone escolhe um novo destino aleatório de tempos em tempos.
        if (attackFrame % 84 == 1) {
            wanderX = GamePanel.ARENA_LEFT + 45
                    + random.nextDouble() * (GamePanel.ARENA_RIGHT - GamePanel.ARENA_LEFT - 90);
            wanderY = GamePanel.ARENA_TOP + 45
                    + random.nextDouble() * (GamePanel.ARENA_BOTTOM - GamePanel.ARENA_TOP - 90);
        }
        moveToward(2, wanderX, wanderY, 1.9);
        if (attackFrame % 55 == 0) {
            game.shootAbstractVolley(cloneX[2], cloneY[2], target);
        }

        // As rajadas mudam de faixa a cada dois segundos e atacam de outra origem.
        int barragePosition = (attackFrame / POSITION_CHANGE_FRAMES) % 4;
        double[] laneX = { GamePanel.ARENA_LEFT + 55, GamePanel.ARENA_RIGHT - 55,
                GamePanel.ARENA_CENTER_X - 135, GamePanel.ARENA_CENTER_X + 135 };
        double[] laneY = { GamePanel.ARENA_TOP + 185, GamePanel.ARENA_TOP + 185,
                GamePanel.ARENA_TOP + 225, GamePanel.ARENA_TOP + 225 };
        if (attackFrame % 45 == 0) {
            game.shootAbstractLane(laneX[barragePosition], laneY[barragePosition],
                    barragePosition % 2 == 0);
        }

        updateStationaryPunish(game, stationaryPlayer != null);

        // Poder próprio: os clones projetam uma grade de linhas lentas em sequência.
        if (attackFrame % SPECIAL_POWER_FRAMES == 0) {
            game.shootAbstractLattice(cloneX[0], cloneY[2]);
        }
    }

    /**
     * Desloca apenas o clone indicado em direção ao destino, mantendo velocidade máxima
     * constante por quadro. Quando o destino está mais perto que esse passo, fixa a posição
     * exatamente no alvo para evitar ultrapassá-lo e oscilar em torno dele.
     */
    private void moveToward(int clone, double targetX, double targetY, double speed) {
        double dx = targetX - cloneX[clone];
        double dy = targetY - cloneY[clone];
        double distance = Math.hypot(dx, dy);
        if (distance <= speed || distance == 0) {
            cloneX[clone] = targetX;
            cloneY[clone] = targetY;
            return;
        }
        cloneX[clone] += dx / distance * speed;
        cloneY[clone] += dy / distance * speed;
    }

    /**
     * Atualiza o ciclo de preparação e recarga do golpe de área. O dano é aplicado uma
     * única vez quando o contador de preparação chega a zero; uma nova preparação só
     * começa quando existe jogador parado e o período de recarga já terminou.
     */
    private void updateStationaryPunish(GamePanel game, boolean playerIsStationary) {
        if (meleeCooldown > 0) meleeCooldown--;
        if (meleeWindup > 0) {
            meleeWindup--;
            if (meleeWindup == 0) {
                game.hitPlayersInRadiusAt(cloneX[1], cloneY[1], MELEE_RADIUS, true);
                meleeCooldown = 54;
            }
        } else if (playerIsStationary && meleeCooldown == 0) {
            meleeWindup = WINDUP_FRAMES;
        }
    }
}
