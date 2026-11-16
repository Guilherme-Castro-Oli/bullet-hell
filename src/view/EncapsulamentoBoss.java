package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Chefe que demonstra encapsulamento ao concentrar posição e temporizadores em
 * estado privado. O painel consulta suas coordenadas e recebe pedidos de dano de
 * área, mas não acessa nem altera diretamente os contadores internos de ataque.
 */
class EncapsulamentoBoss extends GameBoss implements BossBehavior {
    /** Distância máxima entre o centro do golpe e um jogador para aplicar dano. */
    private static final int MELEE_RADIUS = 140;
    /** Tempo de preparação visual antes de aplicar o golpe de área. */
    private static final int WINDUP_FRAMES = 20;
    /** Intervalo entre os golpes de área do chefe. */
    private static final int COOLDOWN_FRAMES = 66;
    /** Começa em recarga para dar ao jogador tempo de reconhecer o padrão do chefe. */
    private int cooldown = COOLDOWN_FRAMES;
    /** Contagem regressiva da preparação visual do golpe; zero significa que não há golpe ativo. */
    private int windup;
    /** Coordenadas privadas usadas tanto para perseguir o alvo como para desenhar o chefe. */
    private double positionX = GamePanel.ARENA_CENTER_X;
    private double positionY = GamePanel.ARENA_CENTER_Y;
    /** Relógio contínuo usado para selecionar os quadros de animação da folha. */
    private int animationFrame;

    /** Inicializa o chefe perseguidor no centro e configura o primeiro ciclo de ataque. */
    EncapsulamentoBoss() {
        super("ENCAPSULAMENTO", "A Caixa Selada", new Color(125, 175, 255), 36);
    }

    /** Retorna a coordenada horizontal atualizada pela perseguição ao alvo. */
    @Override
    public double x() {
        return positionX;
    }

    /** Retorna a coordenada vertical atualizada pela perseguição ao alvo. */
    @Override
    public double y() {
        return positionY;
    }

    /** Move o chefe em direção ao alvo e executa o ataque após o tempo de preparação. */
    @Override
    public void updateBehavior(GamePanel game, Fighter target) {
        animationFrame++;
        // A direção é calculada em relação ao centro do alvo; a divisão normaliza o passo.
        double dx = target.centerX() - positionX;
        double dy = target.centerY() - positionY;
        double distance = Math.hypot(dx, dy);
        if (distance > 1) {
            double step = Math.min(1.7, distance);
            positionX = Math.max(GamePanel.ARENA_LEFT + 35,
                    Math.min(GamePanel.ARENA_RIGHT - 35, positionX + dx / distance * step));
            positionY = Math.max(GamePanel.ARENA_TOP + 35,
                    Math.min(GamePanel.ARENA_BOTTOM - 35, positionY + dy / distance * step));
        }
        // Um golpe em preparação tem prioridade sobre a recarga: ele precisa completar.
        if (windup > 0) {
            windup--;
            if (windup == 0) {
                game.hitPlayersInRadius(this, MELEE_RADIUS, true);
                cooldown = COOLDOWN_FRAMES;
            }
            return;
        }

        if (cooldown > 0) {
            cooldown--;
        } else {
            windup = WINDUP_FRAMES;
        }
    }

    /** Destaca o chefe e mostra o alcance crescente enquanto prepara o golpe. */
    @Override
    public void draw(Graphics2D g) {
        int centerX = (int) x();
        int centerY = (int) y();
        Stroke previousStroke = g.getStroke();

        if (!BossSprites.draw(g, BossSprites.Type.ENCAPSULAMENTO,
                centerX, centerY, animationFrame, 112)) {
            // Círculo de reserva para manter o chefe visível se a imagem não carregar.
            int bodyRadius = 31;
            g.setColor(new Color(125, 175, 255));
            g.fillOval(centerX - bodyRadius, centerY - bodyRadius,
                    bodyRadius * 2, bodyRadius * 2);
            g.setColor(new Color(35, 85, 145, 230));
            g.setStroke(new BasicStroke(2.0f));
            g.drawOval(centerX - bodyRadius, centerY - bodyRadius,
                    bodyRadius * 2, bodyRadius * 2);
        }

        g.setColor(Color.WHITE);
        String label = concept();
        g.drawString(label, centerX - g.getFontMetrics().stringWidth(label) / 2, centerY - 64);
        g.setColor(new Color(50, 20, 20));
        g.fillRect(centerX - 40, centerY + 56, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect(centerX - 40, centerY + 56, 80 * hp() / maxHp(), 7);

        // O mesmo progresso que desenha o anel representa quanto falta para o impacto.
        if (windup > 0) {
            float progress = 1.0f - (float) windup / WINDUP_FRAMES;
            int radius = (int) (MELEE_RADIUS * (0.55 + 0.45 * progress));
            int alpha = 105 + (int) (110 * progress);
            drawPixelRing(g, centerX, centerY, radius, 5,
                    new Color(35, 85, 145, alpha), 10);
            drawPixelRing(g, centerX, centerY, radius - 9, 4,
                    new Color(115, 195, 255, alpha), 5);
        }
        g.setStroke(previousStroke);
    }

    /** Forma o aviso de impacto com blocos luminosos no estilo pixel art dos projéteis. */
    static void drawPixelRing(Graphics2D g, int centerX, int centerY,
                              int radius, int pixelSize, Color color, int thickness) {
        g.setColor(color);
        for (int y = -radius; y <= radius; y += pixelSize) {
            for (int x = -radius; x <= radius; x += pixelSize) {
                double distance = Math.hypot(x + pixelSize / 2.0, y + pixelSize / 2.0);
                if (Math.abs(distance - radius) <= thickness / 2.0) {
                    g.fillRect(centerX + x, centerY + y, pixelSize, pixelSize);
                }
            }
        }
    }
}
