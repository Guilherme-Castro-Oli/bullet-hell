package view;

import model.Boss;
import model.Explosion;
import model.Projectile;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class GamePanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int HUD_HEIGHT = 68;
    private static final int PLAY_HEIGHT = HEIGHT - HUD_HEIGHT;
    static final int ARENA_LEFT = 54;
    static final int ARENA_TOP = 74;
    static final int ARENA_RIGHT = WIDTH - 54;
    static final int ARENA_BOTTOM = PLAY_HEIGHT - 10;
    static final double ARENA_CENTER_X = (ARENA_LEFT + ARENA_RIGHT) / 2.0;
    static final double ARENA_CENTER_Y = (ARENA_TOP + ARENA_BOTTOM) / 2.0;
    private static final int PLAYER_SPEED = 5;
    private static final int PLAYER_FIRE_INTERVAL = 7;
    private static final Rectangle START_BUTTON = new Rectangle(WIDTH - 250, HEIGHT - 105, 220, 80);
    private static final String MENU_IMAGE_RESOURCE = "/image/imagem menu.jpg";

    private enum Screen { MENU, PLAYING, INTERMISSION, PAUSED, VICTORY, DEFEAT }

    private final Timer timer = new Timer(16, this);
    private final BufferedImage background;
    private final BufferedImage menuBackground;
    private final Portal portal;
    private final Fighter playerOne = new Fighter(280, PLAY_HEIGHT - 80, new Color(80, 220, 255));
    private final Fighter playerTwo = new Fighter(500, PLAY_HEIGHT - 80, new Color(255, 115, 180));
    private final List<Projectile> projectiles = new ArrayList<>();
    private final Map<Projectile, Fighter> projectileOwners = new IdentityHashMap<>();
    private final boolean[] keys = new boolean[512];
    private final boolean[] mouseButtons = new boolean[3];
    private Boss[] bosses;
    private int bossIndex;
    private int fireTick;
    private int playerOneBossDamage;
    private int playerTwoBossDamage;
    private double previousPlayerOneX = Double.NaN;
    private double previousPlayerOneY = Double.NaN;
    private double previousPlayerTwoX = Double.NaN;
    private double previousPlayerTwoY = Double.NaN;
    private int playerOneStillFrames;
    private int playerTwoStillFrames;
    private Explosion playerOneExplosion;
    private Explosion playerTwoExplosion;
    private Screen screen = Screen.MENU;
    private boolean showHud = true;
    private boolean startButtonHovered;
    private int screenShakeFrames;
    private int transitionToastFrames;
    private String transitionToast = "";
    private int score;
    private int combo;
    private long lastComboAt;
    private long runStartedAt;
    private long runEndedAt;
    private long pauseStartedAt;
    private long totalPausedMillis;
    private long bestClearTimeMillis = Long.MAX_VALUE;
    private Screen pausedFrom = Screen.PLAYING;

    /**
     * Define o tamanho da arena, carrega os fundos e o portal, registra os controles
     * e inicia o Timer. A cada 16 ms o Timer chama actionPerformed para avançar o jogo.
     */
    GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        background = loadBackground();
        menuBackground = loadMenuBackground();
        portal = new Portal(WIDTH / 2, PLAY_HEIGHT / 2, loadPortalImage());
        configureControls();
        timer.start();
    }

    /**
     * Procura o fundo do menu primeiro como recurso empacotado; se não estiver no
     * classpath, tenta o caminho src usado durante o desenvolvimento. Retorna null
     * nos dois casos de falha para drawMenu pintar o gradiente de reserva.
     */
    private BufferedImage loadMenuBackground() {
        BufferedImage image = loadImage(MENU_IMAGE_RESOURCE);
        if (image != null) {
            return image;
        }
        try {
            return ImageIO.read(new File("src/image/imagem menu.jpg"));
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Procura a imagem do portal no classpath e depois em src/image. Se nenhuma
     * leitura funcionar, retorna null; Portal.draw então desenha uma forma simples.
     */
    private BufferedImage loadPortalImage() {
        BufferedImage image = loadImage("/image/portal_azul.png");
        if (image != null) {
            return image;
        }
        try {
            return ImageIO.read(new File("src/image/portal_azul.png"));
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Abre um recurso de imagem pelo nome absoluto do classpath, como /image/background.png.
     * Trata recurso ausente e erro de leitura retornando null para o chamador decidir
     * qual imagem de reserva usar.
     */
    private BufferedImage loadImage(String resource) {
        try {
            return ImageIO.read(getClass().getResource(resource));
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Carrega a imagem de cenário usada por drawOverworld. Quando ela não existe,
     * informa o problema e retorna null para que drawOverworld desenhe o campo simples.
     */
    private BufferedImage loadBackground() {
        BufferedImage image = loadImage("/image/background.png");
        if (image == null) {
            System.err.println("Não foi possível carregar /image/background.png; usando fundo alternativo.");
        }
        return image;
    }

    /**
     * Liga pressionar/soltar teclas a bind e instala listeners do mouse. Os listeners
     * iniciam o menu, ativam o portal no intervalo, guardam botões de tiro e atualizam
     * o efeito visual do botão START quando o ponteiro entra ou sai da área dele.
     */
    private void configureControls() {
        bind(KeyEvent.VK_W, "w", true);
        bind(KeyEvent.VK_A, "a", true);
        bind(KeyEvent.VK_S, "s", true);
        bind(KeyEvent.VK_D, "d", true);
        bind(KeyEvent.VK_UP, "up", true);
        bind(KeyEvent.VK_DOWN, "down", true);
        bind(KeyEvent.VK_LEFT, "left", true);
        bind(KeyEvent.VK_RIGHT, "right", true);
        bind(KeyEvent.VK_SPACE, "space", true);
        bind(KeyEvent.VK_R, "restart", true);
        bind(KeyEvent.VK_ENTER, "start", true);
        bind(KeyEvent.VK_P, "pause", true);
        bind(KeyEvent.VK_ESCAPE, "menu", true);
        bind(KeyEvent.VK_F3, "hud", true);

        bind(KeyEvent.VK_W, "wUp", false);
        bind(KeyEvent.VK_A, "aUp", false);
        bind(KeyEvent.VK_S, "sUp", false);
        bind(KeyEvent.VK_D, "dUp", false);
        bind(KeyEvent.VK_UP, "upUp", false);
        bind(KeyEvent.VK_DOWN, "downUp", false);
        bind(KeyEvent.VK_LEFT, "leftUp", false);
        bind(KeyEvent.VK_RIGHT, "rightUp", false);
        bind(KeyEvent.VK_SPACE, "spaceUp", false);

        addMouseListener(new MouseAdapter() {
            /** Trata clique no botão do menu, clique para ativar portal e botões de tiro. */
            @Override
            public void mousePressed(MouseEvent event) {
                int button = event.getButton();
                if (screen == Screen.MENU && button == MouseEvent.BUTTON1
                        && START_BUTTON.contains(event.getPoint())) {
                    startGame();
                    return;
                }
                if (screen == Screen.INTERMISSION && button == MouseEvent.BUTTON1) {
                    activatePortal(playerTwo);
                    return;
                }
                if (button >= 1 && button <= mouseButtons.length) {
                    mouseButtons[button - 1] = true;
                }
            }

            /** Libera o estado do botão do mouse para que o jogador pare de atirar. */
            @Override
            public void mouseReleased(MouseEvent event) {
                int button = event.getButton();
                if (button >= 1 && button <= mouseButtons.length) {
                    mouseButtons[button - 1] = false;
                }
            }

            /** Remove o realce do botão START quando o ponteiro sai do painel. */
            @Override
            public void mouseExited(MouseEvent event) {
                startButtonHovered = false;
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            /** Atualiza o realce do botão START e redesenha o menu sob o ponteiro. */
            @Override
            public void mouseMoved(MouseEvent event) {
                startButtonHovered = screen == Screen.MENU && START_BUTTON.contains(event.getPoint());
                repaint();
            }
        });
    }

    /**
     * Registra no InputMap a tecla pressionada ou solta e cria sua ação no ActionMap.
     * A ação trata comandos especiais (iniciar, pausar, menu, HUD e portal); as teclas
     * de movimento e tiro atualizam diretamente o vetor keys, lido em cada quadro.
     * @param keyCode código KeyEvent da tecla
     * @param name identificador da ação Swing
     * @param pressed true para pressionamento, false para soltura
     */
    private void bind(int keyCode, String name, boolean pressed) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyCode, 0, !pressed), name);
        getActionMap().put(name, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            /** Encaminha comandos de tela para seus métodos e grava teclas de movimento/tiro. */
            @Override
            public void actionPerformed(ActionEvent event) {
                if ("start".equals(name)) {
                    if (screen == Screen.MENU || screen == Screen.PAUSED) {
                        if (screen == Screen.MENU) {
                            startGame();
                        } else {
                            resumeGame();
                        }
                    } else if (screen == Screen.VICTORY || screen == Screen.DEFEAT) {
                        startGame();
                    }
                    return;
                }
                if ("space".equals(name) && pressed && screen == Screen.INTERMISSION) {
                    activatePortal(playerOne);
                    return;
                }
                if ("restart".equals(name)) {
                    startGame();
                    return;
                }
                if ("pause".equals(name)) {
                    togglePause();
                    return;
                }
                if ("menu".equals(name)) {
                    showMenu();
                    return;
                }
                if ("hud".equals(name)) {
                    showHud = !showHud;
                    repaint();
                    return;
                }

                // O código da tecla já é o índice usado no vetor de entrada.
                keys[keyCode] = pressed;
            }
        });
    }

    /**
     * Começa uma partida nova: delega a limpeza completa a resetMatch, muda a tela
     * para PLAYING, inicia a contagem de tempo e solicita um redesenho do painel.
     */
    private void startGame() {
        resetMatch();
        screen = Screen.PLAYING;
        runStartedAt = System.currentTimeMillis();
        runEndedAt = 0;
        repaint();
    }

    /**
     * Descarta projéteis e explosões, restaura os dois jogadores, recria os chefes,
     * zera pontuação, combo, pausas e contadores de dano e limpa as entradas ativas.
     * startGame chama este método antes de iniciar o cronômetro da nova partida.
     */
    private void resetMatch() {
        playerOne.reset();
        playerTwo.reset();
        playerOneExplosion = null;
        playerTwoExplosion = null;
        projectiles.clear();
        projectileOwners.clear();
        playerOneBossDamage = 0;
        playerTwoBossDamage = 0;
        playerOneStillFrames = 0;
        playerTwoStillFrames = 0;
        previousPlayerOneX = Double.NaN;
        previousPlayerOneY = Double.NaN;
        previousPlayerTwoX = Double.NaN;
        previousPlayerTwoY = Double.NaN;
        bosses = createBosses();
        bossIndex = 0;
        fireTick = 0;
        score = 0;
        combo = 0;
        lastComboAt = 0;
        totalPausedMillis = 0;
        pauseStartedAt = 0;
        screenShakeFrames = 0;
        transitionToastFrames = 0;
        clearInput();
    }

    /** Muda para MENU, limpa entradas presas e agenda o redesenho da tela inicial. */
    private void showMenu() {
        screen = Screen.MENU;
        clearInput();
        repaint();
    }

    /** Zera os vetores de entrada; é usado ao reiniciar, pausar ou trocar de tela. */
    private void clearInput() {
        Arrays.fill(keys, false);
        Arrays.fill(mouseButtons, false);
    }

    /**
     * Ao pausar PLAYING ou INTERMISSION, guarda a tela de origem e marca o instante
     * da pausa. Ao chamar novamente durante PAUSED, delega a retomada a resumeGame.
     */
    private void togglePause() {
        if (screen == Screen.PLAYING || screen == Screen.INTERMISSION) {
            pausedFrom = screen;
            screen = Screen.PAUSED;
            pauseStartedAt = System.currentTimeMillis();
            clearInput();
        } else if (screen == Screen.PAUSED) {
            resumeGame();
        }
        repaint();
    }

    /**
     * Soma o intervalo pausado ao total excluído do cronômetro, restaura pausedFrom,
     * limpa controles antigos e redesenha para continuar exatamente daquela tela.
     */
    private void resumeGame() {
        totalPausedMillis += System.currentTimeMillis() - pauseStartedAt;
        pauseStartedAt = 0;
        screen = pausedFrom;
        clearInput();
        repaint();
    }

    /**
     * Retorna milissegundos desde runStartedAt, menos pausas acumuladas. Durante a
     * pausa usa pauseStartedAt; após vitória/derrota usa runEndedAt para congelar o tempo.
     */
    private long elapsedMillis() {
        if (runStartedAt == 0) {
            return 0;
        }
        long now = runEndedAt > 0 ? runEndedAt
                : screen == Screen.PAUSED ? pauseStartedAt : System.currentTimeMillis();
        return Math.max(0, now - runStartedAt - totalPausedMillis);
    }

    /**
     * Callback do Timer (aprox. 60 vezes por segundo). No intervalo atualiza jogadores,
     * portal e movimento; em PLAYING atualiza animações, entradas, chefe, ataques,
     * projéteis e transições. Em telas estáticas retorna sem alterar a partida.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (screen == Screen.INTERMISSION) {
            updateExplosions();
            playerOne.update();
            playerTwo.update();
            portal.update();
            if (checkDefeat()) {
                repaint();
                return;
            }
            updatePlayerMovement();
            repaint();
            return;
        }
        if (screen != Screen.PLAYING) {
            return;
        }

        updateExplosions();
        playerOne.update();
        playerTwo.update();
        if (screenShakeFrames > 0) {
            screenShakeFrames--;
        }
        if (transitionToastFrames > 0) {
            transitionToastFrames--;
        }
        if (combo > 0 && System.currentTimeMillis() - lastComboAt > 1800) {
            combo = 0;
        }
        if (checkDefeat()) {
            repaint();
            return;
        }

        updatePlayerMovement();
        Boss boss = bosses[bossIndex];
        boss.update(WIDTH);
        updatePlayerAttacks();
        updateBossAttacks(boss);
        updateProjectiles(boss);
        advanceBoss(boss);
        repaint();
    }

    /**
     * Verifica a condição de derrota. Só troca para DEFEAT quando ambos estão sem vida
     * e as animações de explosão terminaram; retorna true para interromper o quadro atual.
     */
    private boolean checkDefeat() {
        if (playerOne.isDead() && playerTwo.isDead()
                && playerOneExplosion == null && playerTwoExplosion == null) {
            runEndedAt = System.currentTimeMillis();
            screen = Screen.DEFEAT;
            return true;
        }
        return false;
    }

    /**
     * Calcula a direção de cada jogador subtraindo tecla esquerda/cima da direita/baixo,
     * e envia direção, velocidade e limites da arena para Fighter.move.
     */
    private void updatePlayerMovement() {
        double oneX = pressed(KeyEvent.VK_D) - pressed(KeyEvent.VK_A);
        double oneY = pressed(KeyEvent.VK_S) - pressed(KeyEvent.VK_W);
        double twoX = pressed(KeyEvent.VK_RIGHT) - pressed(KeyEvent.VK_LEFT);
        double twoY = pressed(KeyEvent.VK_DOWN) - pressed(KeyEvent.VK_UP);

        playerOne.move(oneX, oneY, PLAYER_SPEED, ARENA_LEFT, ARENA_TOP,
                ARENA_RIGHT, ARENA_BOTTOM);
        playerTwo.move(twoX, twoY, PLAYER_SPEED, ARENA_LEFT, ARENA_TOP,
                ARENA_RIGHT, ARENA_BOTTOM);
    }

    /** Permite subtrair estados de teclas para formar uma direção -1, 0 ou 1. */
    private int pressed(int keyCode) {
        return keys[keyCode] ? 1 : 0;
    }

    /**
     * Avança o contador de tiro e, a cada PLAYER_FIRE_INTERVAL quadros, consulta
     * espaço para o jogador 1 e o botão esquerdo do mouse para o jogador 2.
     */
    private void updatePlayerAttacks() {
        fireTick++;
        if (fireTick % PLAYER_FIRE_INTERVAL != 0) {
            return;
        }

        fire(playerOne, keys[KeyEvent.VK_SPACE]);
        fire(playerTwo, mouseButtons[0]);
    }

    /** Se o botão de tiro está ativo, pede um projétil ao Fighter e registra quem o disparou. */
    private void fire(Fighter fighter, boolean fireButtonDown) {
        if (!fireButtonDown) {
            return;
        }
        Projectile projectile = fighter.fire();
        if (projectile != null) {
            projectiles.add(projectile);
            projectileOwners.put(projectile, fighter);
        }
    }

    /**
     * Consulta o contrato BossBehavior do chefe e chama sua rotina própria com o
     * alvo escolhido; chefes sem esse contrato não executam ataques personalizados.
     */
    private void updateBossAttacks(Boss boss) {
        if (boss instanceof BossBehavior) {
            updatePlayerStillFrames();
            BossBehavior behavior = (BossBehavior) boss;
            behavior.updateBehavior(this, chooseTarget(boss.tick()));
        }
    }

    /** Conta separadamente os quadros em que cada jogador permanece praticamente imóvel. */
    Fighter stationaryTarget() {
        if (playerOneStillFrames >= 313 && playerOne.isAlive()) return playerOne;
        if (playerTwoStillFrames >= 313 && playerTwo.isAlive()) return playerTwo;
        return null;
    }

    private void updatePlayerStillFrames() {
        double oneX = playerOne.centerX();
        double oneY = playerOne.centerY();
        double twoX = playerTwo.centerX();
        double twoY = playerTwo.centerY();
        if (Double.isNaN(previousPlayerOneX)) {
            previousPlayerOneX = oneX;
            previousPlayerOneY = oneY;
            previousPlayerTwoX = twoX;
            previousPlayerTwoY = twoY;
            return;
        }
        playerOneStillFrames = Math.hypot(oneX - previousPlayerOneX,
                oneY - previousPlayerOneY) < 0.75 ? playerOneStillFrames + 1 : 0;
        playerTwoStillFrames = Math.hypot(twoX - previousPlayerTwoX,
                twoY - previousPlayerTwoY) < 0.75 ? playerTwoStillFrames + 1 : 0;
        previousPlayerOneX = oneX;
        previousPlayerOneY = oneY;
        previousPlayerTwoX = twoX;
        previousPlayerTwoY = twoY;
    }

    /** Mede a distância de cada jogador até o chefe e aplica dano aos que estão no raio. */
    void hitPlayersInRadius(Boss boss, double radius, boolean shieldPiercing) {
        hitPlayersInRadiusAt(boss.x(), boss.y(), radius, shieldPiercing);
    }

    /** Aplica o golpe de área a partir de uma posição que pode pertencer a um clone. */
    void hitPlayersInRadiusAt(double centerX, double centerY, double radius, boolean shieldPiercing) {
        if (Math.hypot(playerOne.centerX() - centerX, playerOne.centerY() - centerY) <= radius) {
            applyPlayerDamage(playerOne, shieldPiercing);
        }
        if (Math.hypot(playerTwo.centerX() - centerX, playerTwo.centerY() - centerY) <= radius) {
            applyPlayerDamage(playerTwo, shieldPiercing);
        }
    }

    /**
     * Ignora impactos durante invulnerabilidade; caso contrário aplica dano, ativa
     * tremor de tela e cria uma explosão quando receiveHit confirma a morte.
     */
    private void applyPlayerDamage(Fighter fighter, boolean shieldPiercing) {
        if (!fighter.canReceiveHit()) {
            return;
        }
        screenShakeFrames = Math.max(screenShakeFrames, 9);
        if (fighter.receiveHit(shieldPiercing)) {
            Explosion explosion = new Explosion(fighter.centerX(), fighter.centerY(), fighter.color());
            if (fighter == playerOne) {
                playerOneExplosion = explosion;
            } else {
                playerTwoExplosion = explosion;
            }
        }
    }

    /**
     * Retorna o único jogador vivo, se houver. Com ambos vivos alterna o alvo a cada
     * 45 quadros; durante o terceiro chefe escolhe quem causou mais dano a ele.
     */
    private Fighter chooseTarget(int tick) {
        if (!playerOne.isAlive()) {
            return playerTwo;
        }
        if (!playerTwo.isAlive()) {
            return playerOne;
        }
        if (bossIndex == 2) {
            return playerOneBossDamage >= playerTwoBossDamage ? playerOne : playerTwo;
        }
        return (tick / 45) % 2 == 0 ? playerOne : playerTwo;
    }

    /**
     * Calcula o ângulo do chefe ao alvo e distribui count projéteis pela abertura
     * spread. Cada direção vira um Projectile com as propriedades de divisão e verdade
     * recebidas; os projéteis são acrescentados à lista que updateProjectiles percorre.
     */
    void shootFan(Boss boss, Fighter target, int count, double spread,
                          boolean splits, boolean changesTruth, double speed, Color color) {
        double dx = target.centerX() - boss.x();
        double dy = target.centerY() - boss.y();
        double baseAngle = Math.atan2(dy, dx);

        for (int index = 0; index < count; index++) {
            double offset = count == 1 ? 0 : -spread / 2 + spread * index / (count - 1.0);
            double angle = baseAngle + offset;
            projectiles.add(new Projectile(boss.x(), boss.y() + 25,
                    boss.x() + Math.cos(angle) * 500,
                    boss.y() + Math.sin(angle) * 500,
                    speed, true, color, 7, splits, changesTruth));
        }
    }

    /**
     * Divide 2π por count, cria um projétil em cada ângulo e o adiciona à lista ativa.
     * É usado pelos chefes para ataques circulares independentes da posição dos jogadores.
     */
    void shootRadial(Boss boss, int count, double speed, Color color) {
        for (int index = 0; index < count; index++) {
            double angle = Math.PI * 2 * index / count;
            projectiles.add(new Projectile(boss.x(), boss.y() + 25,
                    boss.x() + Math.cos(angle) * 500,
                    boss.y() + Math.sin(angle) * 500,
                    speed, true, color, 6));
        }
    }

    /** Rajada em faixa: os projéteis seguem paralelos, sem formar leque ou círculo. */
    void shootAbstractVolley(double originX, double originY, Fighter target) {
        double baseAngle = Math.atan2(target.centerY() - originY,
                target.centerX() - originX);
        for (int index = -1; index <= 1; index++) {
            double angle = baseAngle + index * 0.24;
            projectiles.add(new Projectile(originX, originY,
                    originX + Math.cos(angle) * 500,
                    originY + Math.sin(angle) * 500,
                    3.8, true, new Color(255, 195, 90), 6));
        }
    }

    /** Rajada em faixa: os projéteis seguem paralelos, sem formar leque ou círculo. */
    void shootAbstractLane(double originX, double originY, boolean horizontal) {
        for (int index = -3; index <= 3; index++) {
            double startX = horizontal ? originX - 190 : originX + index * 24;
            double startY = horizontal ? originY + index * 24 : originY - 150;
            double aimX = horizontal ? originX + 300 : startX;
            double aimY = horizontal ? startY : originY + 190;
            projectiles.add(new Projectile(startX, startY, aimX, aimY,
                    4.0, true, new Color(205, 125, 255), 6));
        }
    }

    /** Poder especial: os clones projetam cortinas paralelas que cruzam a arena em grade. */
    void shootAbstractLattice(double leftX, double rightY) {
        int lanes = 9;
        for (int index = 0; index < lanes; index++) {
            double fraction = index / (double) (lanes - 1);
            double y = ARENA_TOP + fraction * (ARENA_BOTTOM - ARENA_TOP);
            projectiles.add(new Projectile(leftX, y, ARENA_RIGHT, y,
                    3.4, true, new Color(220, 150, 255), 7));

            double x = ARENA_LEFT + fraction * (ARENA_RIGHT - ARENA_LEFT);
            projectiles.add(new Projectile(x, rightY, x, ARENA_BOTTOM,
                    3.4, true, new Color(150, 210, 255), 7));
        }
    }

    /**
     * Percorre os projéteis com Iterator para poder removê-los com segurança. Atualiza
     * posição, elimina os que saíram da tela, troca projéteis divididos por oito novos,
     * aplica dano ao chefe ou aos jogadores e só depois acrescenta os novos projéteis.
     */
    private void updateProjectiles(Boss boss) {
        Iterator<Projectile> iterator = projectiles.iterator();
        // A lista temporária só é necessária nos quadros em que um projétil se divide.
        List<Projectile> splitProjectiles = null;

        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update();

            if (projectile.outside(WIDTH, HEIGHT)) {
                projectileOwners.remove(projectile);
                iterator.remove();
                continue;
            }

            if (projectile.consumeSplitReady()) {
                if (splitProjectiles == null) {
                    splitProjectiles = new ArrayList<>(8);
                }
                addSplitProjectiles(projectile, splitProjectiles);
                iterator.remove();
                continue;
            }

            boolean hitBoss = boss instanceof AbstracaoBoss
                    ? ((AbstracaoBoss) boss).isHitAt(projectile.x(), projectile.y())
                    : Math.hypot(projectile.x() - boss.x(), projectile.y() - boss.y()) < 35;
            if (!projectile.fromBoss() && !boss.defeated() && hitBoss) {
                boolean hitOriginal = !(boss instanceof AbstracaoBoss)
                        || ((AbstracaoBoss) boss).isOriginalHitAt(projectile.x(), projectile.y());
                if (hitOriginal) {
                    boss.damage();
                    Fighter owner = projectileOwners.remove(projectile);
                    if (owner == playerOne) {
                        playerOneBossDamage++;
                    } else if (owner == playerTwo) {
                        playerTwoBossDamage++;
                    }
                    registerBossHit();
                } else {
                    projectileOwners.remove(projectile);
                }
                iterator.remove();
                continue;
            }

            if (projectile.fromBoss() && projectile.isHarmful()) {
                boolean shieldPiercing = "ENCAPSULAMENTO".equals(boss.concept());
                if (hitPlayer(projectile, playerOne, iterator, shieldPiercing)) {
                    continue;
                }
                hitPlayer(projectile, playerTwo, iterator, shieldPiercing);
            }
        }

        if (splitProjectiles != null) {
            projectiles.addAll(splitProjectiles);
        }
    }

    /** Registra o instante do acerto, inicia/continua o combo e soma pontos proporcionais a ele. */
    private void registerBossHit() {
        long now = System.currentTimeMillis();
        combo = now - lastComboAt <= 1800 ? combo + 1 : 1;
        lastComboAt = now;
        score += 10 + Math.min(combo, 20);
    }

    /**
     * Cria oito trajetórias igualmente espaçadas a partir da posição do projétil original
     * e as guarda na lista temporária, evitando modificar projectiles durante sua iteração.
     */
    private void addSplitProjectiles(Projectile source, List<Projectile> destination) {
        for (int index = 0; index < 8; index++) {
            double angle = Math.PI * 2 * index / 8;
            destination.add(new Projectile(source.x(), source.y(),
                    source.x() + Math.cos(angle) * 100,
                    source.y() + Math.sin(angle) * 100,
                    3.8, true, new Color(255, 185, 70), 6));
        }
    }

    /**
     * Compara a distância projétil-jogador com o raio de colisão. Retorna false sem alterar
     * o Iterator quando não há acerto; no acerto, aplica dano, remove o projétil e retorna true.
     */
    private boolean hitPlayer(Projectile projectile, Fighter fighter,
                              Iterator<Projectile> iterator, boolean shieldPiercing) {
        if (!fighter.isAlive()
                || Math.hypot(projectile.x() - fighter.centerX(),
                              projectile.y() - fighter.centerY()) >= 22) {
            return false;
        }

        applyPlayerDamage(fighter, shieldPiercing);
        // O projétil é consumido mesmo durante a invulnerabilidade temporária.
        iterator.remove();
        return true;
    }

    /**
     * Não faz nada enquanto o chefe vive. Ao derrotá-lo, limpa projéteis e avança o índice;
     * registra vitória se era o último chefe ou muda para INTERMISSION e limpa controles.
     */
    private void advanceBoss(Boss boss) {
        if (!boss.defeated()) {
            return;
        }

        bossIndex++;
        projectiles.clear();
        projectileOwners.clear();
        if (bossIndex == 2) {
            playerOneBossDamage = 0;
            playerTwoBossDamage = 0;
        }
        if (bossIndex == bosses.length) {
            runEndedAt = System.currentTimeMillis();
            screen = Screen.VICTORY;
            bestClearTimeMillis = Math.min(bestClearTimeMillis, elapsedMillis());
        } else {
            screen = Screen.INTERMISSION;
            transitionToastFrames = 0;
            clearInput();
        }
    }

    /**
     * Valida tela e distância do jogador ao portal. Se ambas forem válidas, retoma PLAYING,
     * prepara o aviso com o nome do próximo chefe e limpa teclas para evitar movimento preso.
     */
    private void activatePortal(Fighter activator) {
        if (screen != Screen.INTERMISSION) {
            return;
        }
        if (!portal.isPlayerNear(activator)) {
            return;
        }

        screen = Screen.PLAYING;
        transitionToast = "PRÓXIMO CHEFE: " + bosses[bossIndex].name();
        transitionToastFrames = 100;
        clearInput();
        repaint();
    }

    /** Atualiza cada explosão existente e apaga sua referência quando a animação termina. */
    private void updateExplosions() {
        if (playerOneExplosion != null) {
            playerOneExplosion.update();
            if (playerOneExplosion.isFinished()) {
                playerOneExplosion = null;
            }
        }
        if (playerTwoExplosion != null) {
            playerTwoExplosion.update();
            if (playerTwoExplosion.isFinished()) {
                playerTwoExplosion = null;
            }
        }
    }

    /**
     * Pinta uma cópia do contexto gráfico: menu retorna após desenhar sua tela; nas demais
     * telas desenha cenário, portal/chefe, projéteis, jogadores, HUD, aviso e overlays nessa
     * ordem. A cópia é descartada ao final para não alterar o contexto do Swing.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (screen == Screen.MENU) {
            drawMenu(g);
            g.dispose();
            return;
        }

        if (screen == Screen.PLAYING && screenShakeFrames > 0) {
            int offset = screenShakeFrames % 2 == 0 ? 3 : -3;
            g.translate(offset, -offset / 2);
        }
        drawOverworld(g);
        if (isIntermissionScreen()) {
            portal.draw(g, portal.isPlayerNear(playerOne, playerTwo));
        } else if (bossIndex < bosses.length) {
            bosses[bossIndex].draw(g);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }

        if (playerOneExplosion != null) {
            playerOneExplosion.draw(g);
        } else {
            playerOne.draw(g);
        }
        if (playerTwoExplosion != null) {
            playerTwoExplosion.draw(g);
        } else {
            playerTwo.draw(g);
        }

        if (showHud) {
            drawBattleHeader(g);
            drawHud(g);
        }
        if (transitionToastFrames > 0 && screen == Screen.PLAYING) {
            drawTransitionToast(g);
        }
        if (screen == Screen.PAUSED) {
            drawOverlay(g, "PAUSADO");
        } else if (screen == Screen.VICTORY) {
            drawOverlay(g, "VOCÊ VENCEU!");
        } else if (screen == Screen.DEFEAT) {
            drawOverlay(g, "FIM DE JOGO");
        }
        g.dispose();
    }

    /** Decide se paintComponent deve desenhar o portal nesta tela ou numa pausa do intervalo. */
    private boolean isIntermissionScreen() {
        return screen == Screen.INTERMISSION
                || (screen == Screen.PAUSED && pausedFrom == Screen.INTERMISSION);
    }

    /**
     * Desenha o fundo do menu (imagem ou gradiente), aplica sombra para contraste,
     * renderiza título e botão. startButtonHovered escolhe as cores do gradiente;
     * o listener do mouse mantém esse estado atualizado.
     */
    private void drawMenu(Graphics2D g) {
        if (menuBackground != null) {
            drawCover(g, menuBackground);
        } else {
            g.setPaint(new GradientPaint(0, 0, new Color(12, 18, 48),
                    WIDTH, HEIGHT, new Color(90, 25, 75)));
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Escurece levemente a imagem para dar contraste sem substituir o fundo original.
        g.setPaint(new GradientPaint(0, 0, new Color(5, 12, 32, 175),
                0, HEIGHT, new Color(8, 18, 35, 75)));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(105, 225, 255, 145));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(22, 22, WIDTH - 44, HEIGHT - 44, 28, 28);

        String title = "POO HELL";
        g.setFont(new Font("SansSerif", Font.BOLD, 56));
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = (WIDTH - titleWidth) / 2;
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(title, titleX + 3, 153);
        g.setColor(new Color(115, 235, 255));
        g.drawString(title, titleX, 150);

        int buttonX = START_BUTTON.x + 7;
        int buttonY = START_BUTTON.y + 8;
        int buttonWidth = START_BUTTON.width - 14;
        int buttonHeight = START_BUTTON.height - 16;
        g.setColor(new Color(0, 0, 0, 105));
        g.fillRoundRect(buttonX + 3, buttonY + 5, buttonWidth, buttonHeight, 24, 24);
        g.setPaint(new GradientPaint(buttonX, buttonY,
                startButtonHovered ? new Color(100, 245, 255, 240) : new Color(55, 190, 225, 225),
                buttonX, buttonY + buttonHeight,
                startButtonHovered ? new Color(35, 115, 190, 240) : new Color(30, 75, 145, 230)));
        g.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 24, 24);
        g.setColor(new Color(185, 250, 255, 230));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 24, 24);

        g.setFont(new Font("SansSerif", Font.BOLD, 27));
        g.setColor(Color.WHITE);
        String label = "START";
        int labelX = buttonX + (buttonWidth - g.getFontMetrics().stringWidth(label)) / 2;
        int labelY = buttonY + (buttonHeight - g.getFontMetrics().getHeight()) / 2
                + g.getFontMetrics().getAscent();
        g.drawString(label, labelX, labelY);
    }

    /**
     * Calcula a maior escala necessária para cobrir largura e altura sem deformar a imagem,
     * então centraliza o excedente; as partes fora do painel são cortadas pelo Swing.
     * @param g contexto gráfico do painel
     * @param image imagem a ajustar
     */
    private void drawCover(Graphics2D g, BufferedImage image) {
        double scale = Math.max((double) WIDTH / image.getWidth(),
                (double) HEIGHT / image.getHeight());
        int drawWidth = (int) Math.ceil(image.getWidth() * scale);
        int drawHeight = (int) Math.ceil(image.getHeight() * scale);
        int x = (WIDTH - drawWidth) / 2;
        int y = (HEIGHT - drawHeight) / 2;
        g.drawImage(image, x, y, drawWidth, drawHeight, null);
    }

    /**
     * Se existe um chefe ativo, desenha no cabeçalho seu índice/nome, tempo decorrido,
     * pontuação e combo. O tempo passa por formatTime e os textos ficam alinhados às bordas.
     */
    private void drawBattleHeader(Graphics2D g) {
        if (bossIndex >= bosses.length) {
            return;
        }
        g.setColor(new Color(5, 9, 22, 185));
        g.fillRoundRect(176, 8, WIDTH - 352, 34, 14, 14);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        String bossLabel = "CHEFE " + (bossIndex + 1) + "/" + bosses.length
                + "  •  " + bosses[bossIndex].name();
        g.drawString(bossLabel, WIDTH / 2 - g.getFontMetrics().stringWidth(bossLabel) / 2, 30);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("TEMPO " + formatTime(elapsedMillis()), 18, 28);
        String stats = "PONTOS " + score + (combo > 1 ? "  •  COMBO x" + combo : "");
        g.drawString(stats, WIDTH - 18 - g.getFontMetrics().stringWidth(stats), 28);
    }

    /**
     * Desenha o aviso temporário de próximo chefe centralizado na parte superior da arena.
     * paintComponent chama este método enquanto o contador, reduzido em actionPerformed,
     * está ativo e a tela está em jogo. activatePortal define texto e duração; aqui a largura
     * da fonte calcula o X para centralizar a mensagem dentro do painel arredondado.
     */
    private void drawTransitionToast(Graphics2D g) {
        g.setColor(new Color(8, 15, 28, 205));
        g.fillRoundRect(184, 88, WIDTH - 368, 38, 14, 14);
        g.setColor(new Color(255, 225, 105));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        int textX = (WIDTH - g.getFontMetrics().stringWidth(transitionToast)) / 2;
        g.drawString(transitionToast, textX, 113);
    }

    /**
     * paintComponent chama este método em pausa, vitória ou derrota. Ele escurece o quadro
     * já desenhado e centraliza o título recebido; só na vitória acrescenta tempo da partida
     * e melhor tempo da sessão. Não muda o estado do jogo, apenas desenha a camada superior.
     */
    private void drawOverlay(Graphics2D g, String title) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 38));
        g.drawString(title, WIDTH / 2 - g.getFontMetrics().stringWidth(title) / 2, HEIGHT / 2 - 10);
        if (screen == Screen.VICTORY) {
            String result = "TEMPO: " + formatTime(elapsedMillis())
                    + "  •  MELHOR: " + formatTime(bestClearTimeMillis);
            int resultX = (WIDTH - g.getFontMetrics().stringWidth(result)) / 2;
            g.drawString(result, resultX, HEIGHT / 2 + 54);
        }
    }

    /**
     * Converte milissegundos em minutos e segundos no formato MM:SS; é chamado pelo
     * cabeçalho da partida e pelo resumo de vitória.
     * Descarta frações de segundo, impede valores negativos e mantém minutos acima de 59
     * (por exemplo, 3.600.000 ms resulta em 60:00). A montagem manual evita criar um
     * formatador localizado toda vez que o HUD é redesenhado.
     */
    private String formatTime(long millis) {
        long seconds = Math.max(0, millis) / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return (minutes < 10 ? "0" : "") + minutes + ":"
                + (remainingSeconds < 10 ? "0" : "") + remainingSeconds;
    }

    /** Cria o fundo e a borda do HUD inferior e delega os indicadores a cada Fighter. */
    private void drawHud(Graphics2D g) {
        int top = HEIGHT - HUD_HEIGHT + 7;
        g.setColor(new Color(15, 24, 25, 225));
        g.fillRect(16, top, WIDTH - 32, HUD_HEIGHT - 14);
        g.setColor(new Color(220, 230, 210, 120));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(16, top, WIDTH - 32, HUD_HEIGHT - 14);
        g.setColor(new Color(220, 230, 210, 75));
        g.drawLine(25, top + 4, WIDTH - 25, top + 4);
        drawPlayerHud(g, 30, top + 19, "P1", playerOne);
        drawPlayerHud(g, 410, top + 19, "P2", playerTwo);
    }

    /**
     * Usa os dados do Fighter para desenhar vidas, escudos disponíveis/em recarga e a
     * barra de stamina. x/y definem a origem do bloco; label identifica P1 ou P2.
     */
    private void drawPlayerHud(Graphics2D g, int x, int y, String label, Fighter fighter) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(fighter.color());
        g.drawString(label, x, y);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(new Color(235, 238, 228));
        g.drawString("VIDA", x + 38, y);
        for (int index = 0; index < Fighter.MAX_LIVES; index++) {
            int blockX = x + 72 + index * 15;
            g.setColor(index < fighter.lives() ? fighter.color() : new Color(75, 82, 80));
            g.fillRect(blockX, y - 10, 11, 9);
            g.setColor(new Color(235, 238, 228, 130));
            g.drawRect(blockX, y - 10, 10, 8);
        }

        g.setColor(new Color(235, 238, 228));
        g.drawString("ESCUDO", x + 152, y);
        for (int index = 0; index < Fighter.MAX_SHIELDS; index++) {
            int blockX = x + 195 + index * 14;
            g.setColor(new Color(45, 64, 67));
            g.fillRect(blockX, y - 10, 10, 9);
            if (index < fighter.shields()) {
                g.setColor(new Color(80, 220, 255));
                g.fillRect(blockX, y - 10, 10, 9);
            } else if (index == fighter.shields()) {
                int progress = (int) (10 * fighter.shieldRegenerationProgress());
                if (progress > 0) {
                    g.setColor(new Color(80, 220, 255, 190));
                    g.fillRect(blockX, y - 10, progress, 9);
                }
            }
            g.setColor(new Color(225, 245, 255, 145));
            g.drawRect(blockX, y - 10, 9, 8);
        }

        g.setColor(new Color(235, 238, 228));
        g.drawString("STAMINA", x + 38, y + 25);
        int barX = x + 96;
        int barY = y + 15;
        int barWidth = 245;
        g.setColor(new Color(55, 65, 64));
        g.fillRect(barX, barY, barWidth, 10);
        int filledSegments = (int) Math.ceil(fighter.stamina() / 100.0 * 20);
        for (int segment = 0; segment < 20; segment++) {
            int segmentX = barX + 2 + segment * 12;
            g.setColor(segment < filledSegments
                    ? fighter.stamina() < 25 ? new Color(245, 175, 70) : fighter.color()
                    : new Color(75, 85, 83));
            g.fillRect(segmentX, barY + 2, 10, 6);
        }
        g.setColor(new Color(235, 238, 228, 190));
        g.drawRect(barX, barY, barWidth, 10);
    }

    /**
     * Desenha a imagem de cenário em toda a janela; se o recurso não foi carregado,
     * monta um campo verde quadriculado simples para que a partida ainda seja jogável.
     */
    private void drawOverworld(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
            return;
        }

        g.setColor(new Color(91, 143, 57));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(102, 157, 62));
        for (int y = 45; y < HEIGHT; y += 48) {
            for (int x = ((y / 48) % 2) * 24; x < WIDTH; x += 48) {
                g.fillRect(x, y, 24, 24);
            }
        }
        g.setColor(new Color(225, 210, 145));
        g.fillRect(0, 38, WIDTH, 3);
    }

    /** Retorna os quatro chefes na ordem da partida; resetMatch cria novas instâncias. */
    private static Boss[] createBosses() {
        return new Boss[] {
                new HerancaBoss(),
                new PolimorfismoBoss(),
                new EncapsulamentoBoss(),
                new AbstracaoBoss()
        };
    }
}
