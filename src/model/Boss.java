package model;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Representa um chefe. Cada instância guarda nome, conceito, cor e vida.
 * O conceito também escolhe a forma desenhada, servindo de demonstração
 * visual simples de como um mesmo método pode produzir resultados diferentes.
 */
public abstract class Boss {
    private final String concept, name;
    private final Color color;
    private final int maxHp;
    private int hp;
    private double x, y;
    private int tick;

    /** Recebe os valores que diferenciam este chefe dos outros. */
    public Boss(String concept, String name, Color color, int hp) {
        this.concept = concept; this.name = name; this.color = color;
        this.hp = hp; this.maxHp = hp;
        x = 400; y = 185;
    }

    public void update(int width) {
        tick++;
        // O chefe se desloca lateralmente enquanto ataca. Seno cria movimento
        // contínuo de ida e volta; tick funciona como relógio em quadros.
        x = width / 2.0 + Math.sin(tick * 0.025) * Math.min(180, width * 0.25);
    }

    // O resto da divisão é zero nos quadros em que chegou a hora de atirar.
    public boolean shouldShoot(int interval) { return tick % interval == 0; }
    // Só a classe Boss altera sua própria vida; a tela pede dano por este método.
    public void damage() { hp--; }
    public boolean defeated() { return hp <= 0; }
    public int tick() { return tick; }
    public double x() { return x; }
    public double y() { return y; }
    public String name() { return name; }
    public String concept() { return concept; }
    public int hp() { return hp; }
    public int maxHp() { return maxHp; }

    public void draw(Graphics2D g) {
        int radius = 31;
        g.setColor(color);
        // Cada caso desenha uma forma diferente. Todos os chefes usam draw(),
        // mas o resultado visual depende do conceito guardado no objeto.
        switch (concept) {
            case "HERANÇA":
                g.fillRect((int)x-radius, (int)y-radius, radius*2, radius*2); break;
            case "POLIMORFISMO":
                int[] px={(int)x,(int)x+radius,(int)x,(int)x-radius};
                int[] py={(int)y-radius,(int)y,(int)y+radius,(int)y};
                g.fillPolygon(px, py, 4); break;
            case "ENCAPSULAMENTO":
                g.fillOval((int)x-radius, (int)y-radius, radius*2, radius*2); break;
            default:
                g.fillRoundRect((int)x-radius, (int)y-radius, radius*2, radius*2, 12, 12);
        }
        g.setColor(Color.WHITE);
        // O título mostra somente o conceito de POO, conforme o nome do chefe.
        g.drawString(concept, (int)x - g.getFontMetrics().stringWidth(concept) / 2, (int)y - 40);
        g.setColor(new Color(50, 20, 20)); g.fillRect((int)x-40, (int)y+38, 80, 7);
        g.setColor(new Color(255, 220, 70));
        g.fillRect((int)x-40, (int)y+38, 80 * hp / maxHp, 7);
    }

    
}
