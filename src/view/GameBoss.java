package view;

import java.awt.Color;

import model.Player;
import model.Projectile;

/**
 * Adapta o chefe simples do pacote model ao contrato usado pela arena atual.
 * O modelo original continua sendo dono de nome, cor, vida e estado de morte;
 * esta camada acrescenta o conceito didático e as consultas que GamePanel usa
 * para escolher alvos, desenhar a interface e avançar o ciclo de cada chefe.
 * Chefes concretos definem a aparência e podem substituir coordenadas ou relógio.
 */
abstract class GameBoss extends model.Boss {
    /** Nome do conceito exibido no HUD e consultado por regras específicas da arena. */
    private final String concept;
    /** Relógio geral incrementado pelo painel, usado por chefes sem relógio próprio. */
    private int frame;

    /** Inicializa o estado herdado do modelo e guarda o conceito específico da partida. */
    GameBoss(String concept, String name, Color color, int maximumHealth) {
        super(name, color, maximumHealth,
                GamePanel.ARENA_CENTER_X, GamePanel.ARENA_CENTER_Y);
        this.concept = concept;
    }

    /** Avança o relógio compartilhado; as subclasses atualizam seus próprios movimentos e ataques. */
    public void update(int width) {
        frame++;
    }

    /** Permite que o contrato do modelo antigo avance pelo mesmo ponto de atualização. */
    @Override
    public void move(int width) {
        update(width);
    }

    /** Fornece um tiro básico para compatibilidade com o contrato abstrato do modelo. */
    @Override
    public Projectile shoot(Player player) {
        return new Projectile(x(), y(), player.centerX(), player.centerY(),
                true, color, 7);
    }

    /** Devolve o nome público do chefe sem expor outros campos mutáveis da classe base. */
    public String name() {
        return name;
    }

    /** Devolve a identidade conceitual usada por mensagens, HUD e regras de combate. */
    public String concept() {
        return concept;
    }

    /** Devolve a coordenada horizontal; chefes móveis podem sobrescrever esta consulta. */
    public double x() {
        return x;
    }

    /** Devolve a coordenada vertical; chefes móveis podem sobrescrever esta consulta. */
    public double y() {
        return y;
    }

    /** Relógio usado pelo seletor de alvo e pelos padrões de ataque sem contador próprio. */
    public int tick() {
        return frame;
    }

    /** Vida atual para desenhar barras e ajustar os ataques conforme o dano acumulado. */
    public int hp() {
        return hp;
    }

    /** Vida inicial para calcular proporções e condições de fase do chefe. */
    public int maxHp() {
        return maxHP;
    }

    /** Informa se a vida chegou a zero, evitando atualizar ou desenhar um chefe derrotado. */
    public boolean defeated() {
        return dead || hp <= 0;
    }

    /** Aplica uma unidade de dano e sincroniza o estado de morte mantido pelo modelo. */
    public void damage() {
        if (defeated()) {
            return;
        }
        hp--;
        if (hp <= 0) {
            dead = true;
        }
    }
}
