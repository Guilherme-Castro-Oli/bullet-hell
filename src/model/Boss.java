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
    protected final int radius = 31;
    protected int maxHP;
    protected int hp;
    protected double x, y;
    protected int cooldown;
    protected int fireRate;
    protected boolean dead = false;

    /** Recebe os valores que diferenciam este chefe dos outros. */
    public Boss(String name, Color color, int maxhp, double x, double y, int fireRate) {
        this.name = name; this.color = color; this.maxHP = maxhp; 
        this.hp = maxHP; this.x = x; this.y = y; this.fireRate = fireRate;
    }

    // movimentação
    abstract public void move(int width);

    abstract public void draw(Graphics2D g);

    public boolean hit(Projectile proj){
        if (proj.getFromBoss() || dead) {
            return false;
        }
        if (proj.getX() < x + radius && proj.getX() + proj.getRadius() > x - radius &&
            proj.getY() < y + radius && proj.getY() + proj.getRadius() > y - radius) {
            hp--;
            if (hp <= 0) {
                dead = true;
            }
            return true;
        }
        return false;
    }

    abstract public Projectile shoot(Player player);

    public boolean shouldShoot(){
        if (dead) {
            return false;
        }
        if (++cooldown < fireRate) {
            return false;
        }
        cooldown = 0;
        return true;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getmaxHp() { return maxHP; }
    
}
