package model;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Representa um chefe. Cada instância guarda nome, conceito, cor e vida.
 * O conceito também escolhe a forma desenhada, servindo de demonstração
 * visual simples de como um mesmo método pode produzir resultados diferentes.
 */
public abstract class Boss {
    protected final String name;
    protected final Color color;
    protected int maxHP;
    protected int hp = maxHP;
    protected double x, y;
    protected int cooldown;

    /** Recebe os valores que diferenciam este chefe dos outros. */
    public Boss(String name, Color color, int maxhp, double x, double y) {
        this.name = name; this.color = color;
        this.maxHP = maxhp; this.x = x; this.y = y;
    }

    // movimentação
    abstract public void move(int width);
    /* coisa pra movimento do heranca
    tick++;
        // O chefe se desloca lateralmente enquanto ataca. Seno cria movimento
        // contínuo de ida e volta; tick funciona como relógio em quadros.
        x = width / 2.0 + Math.sin(tick * 0.025) * Math.min(180, width * 0.25);
        */


    // O resto da divisão é zero nos quadros em que chegou a hora de atirar.
    //abstract public boolean shouldShoot();

    abstract public void draw(Graphics2D g);

    public double getX() { return x; }
    public double getY() { return y; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getmaxHp() { return maxHP; }
    
}
