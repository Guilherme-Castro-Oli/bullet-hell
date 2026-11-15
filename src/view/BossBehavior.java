package view;

interface BossBehavior {
    /** Recebe o painel para criar ataques e o alvo selecionado para este quadro. */
    void updateBehavior(GamePanel game, Fighter target);
}
