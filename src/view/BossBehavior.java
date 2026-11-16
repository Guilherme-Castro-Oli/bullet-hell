package view;

/**
 * Contrato adicional para chefes que possuem rotinas de ataque próprias.
 * A classe base Boss representa estado e operações comuns; esta interface permite
 * que GamePanel identifique quais chefes precisam de uma atualização especializada
 * sem embutir cada padrão de ataque no painel que coordena a partida.
 */
interface BossBehavior {
    /**
     * Atualiza o comportamento especial por um quadro.
     * @param game painel que fornece operações compartilhadas para criar ataques e atingir jogadores
     * @param target jogador selecionado pelo painel como alvo deste quadro
     */
    void updateBehavior(GamePanel game, Fighter target);
}
