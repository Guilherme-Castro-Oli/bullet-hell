package model;

import java.awt.Color;
import java.awt.Graphics2D;

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

    /** Cria um jogador na posição indicada e com uma cor fácil de reconhecer. */
    public Player(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Soma o deslocamento pedido à posição atual.
     * width e height vêm da área de jogo; usamos size para impedir que
     * qualquer parte do quadrado ultrapasse suas bordas.
     */
    public void move(double dx, double dy, int minx, int miny, int maxx, int maxy) {
        // Math.min limita o máximo e Math.max limita o mínimo da coordenada.
        x = Math.max(minx, Math.min(maxx - size, x + dx));
        // A coordenada 40 deixa livre a faixa superior usada pelo HUD.
        y = Math.max(miny, Math.min(maxy - size, y + dy));
    }

    /** Desenha o quadrado preenchido e uma borda branca para destacá-lo. */
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, size, size);
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y, size, size);
    }

    // Os tiros partem do centro do quadrado, não de um de seus cantos.
    public double centerX() { return x + size / 2.0; }
    public double centerY() { return y + size / 2.0; }
    public Color getColor() { return color; }
}
