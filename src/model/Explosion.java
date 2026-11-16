package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

/** Efeito visual de partículas quadradas criado quando um jogador perde uma vida. */
public class Explosion {
    /** Quantidade de quadros durante a qual o efeito permanece visível. */
    private static final int DURATION = 42;
    /** Coordenadas atuais de cada partícula; os índices correspondem entre todos os vetores. */
    private final double[] x = new double[16];
    private final double[] y = new double[16];
    /** Deslocamento aplicado por quadro; vy recebe gravidade durante a animação. */
    private final double[] vx = new double[16];
    private final double[] vy = new double[16];
    /** Cor individual de cada partícula, escolhida da paleta no construtor. */
    private final Color[] colors = new Color[16];
    /** Número de atualizações já processadas, usado para calcular o desvanecimento. */
    private int age = 0;

    /** Espalha 16 partículas em todas as direções a partir do centro do jogador. */
    public Explosion(double centerX, double centerY, Color playerColor) {
        // A paleta mantém as partículas visualmente ligadas ao jogador que foi atingido.
        Color[] palette = { playerColor, Color.WHITE, new Color(255, 205, 70), new Color(255, 105, 55) };
        for (int i = 0; i < x.length; i++) {
            // Distribui os ângulos por uma volta completa; velocidades variadas quebram a simetria rígida.
            double angle = Math.PI * 2 * i / x.length;
            double speed = 2.0 + (i % 4) * 0.7;
            x[i] = centerX;
            y[i] = centerY;
            vx[i] = Math.cos(angle) * speed;
            vy[i] = Math.sin(angle) * speed;
            colors[i] = palette[i % palette.length];
        }
    }

    /** Avança as partículas e aplica uma leve gravidade para dar peso à explosão. */
    public void update() {
        age++;
        for (int i = 0; i < x.length; i++) {
            // Primeiro integra a velocidade atual; depois aumenta vy para afetar os quadros seguintes.
            x[i] += vx[i];
            y[i] += vy[i];
            vy[i] += 0.08;
        }
    }

    /** Desenha partículas quadradas que ficam transparentes até desaparecer. */
    public void draw(Graphics2D g) {
        // O progresso é limitado a zero para que o alpha permaneça válido no fim do ciclo.
        float alpha = Math.max(0, 1.0f - (float) age / DURATION);
        // O contexto derivado permite aplicar transparência sem alterar a pintura do painel chamador.
        Graphics2D effect = (Graphics2D) g.create();
        effect.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        for (int i = 0; i < x.length; i++) {
            effect.setColor(colors[i]);
            int size = 5 + (i % 3) * 2;
            effect.fillRect((int) x[i] - size / 2, (int) y[i] - size / 2, size, size);
        }
        effect.dispose();
    }

    /**
     * Informa ao painel se o efeito já consumiu todos os quadros de duração.
     * A resposta permite remover a referência da explosão e encerrar sua atualização.
     */
    public boolean isFinished() { return age >= DURATION; }
}
