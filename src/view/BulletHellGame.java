package view;

import model.Boss;
import model.Explosion;
import model.Player;
import model.Projectile;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
<<<<<<< HEAD
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
=======
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
>>>>>>> 1c870c6 (test)
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
<<<<<<< HEAD
import java.util.Iterator;
import java.util.List;

/** Janela e coordenação da partida de dois jogadores. */
public class BulletHellGame extends JFrame {
    private static final long serialVersionUID = 1L;

=======
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Janela Swing que inicia a partida e contém toda a lógica de apresentação do jogo. */
public class BulletHellGame extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Configura a janela do jogo: define o título, fecha o processo ao sair,
     * instala o GamePanel, ajusta o tamanho ao painel e centraliza a janela.
     * Toda a lógica de partida começa dentro do painel instalado aqui.
     */
>>>>>>> 1c870c6 (test)
    public BulletHellGame() {
        super("POO Hell — Arena dos Conceitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

<<<<<<< HEAD
    /** Permite iniciar esta tela diretamente pela classe BulletHellGame. */
        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BulletHellGame().setVisible(true));
    }

    /** O painel coordena entrada, regras da partida e desenho. */
=======
    /**
     * Ponto de entrada para abrir esta tela diretamente. O invokeLater agenda a criação
     * da janela na thread de eventos do Swing, onde eventos e desenho devem ocorrer.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BulletHellGame().setVisible(true));
    }

        /** Painel central: guarda o estado da partida, trata entradas, atualiza regras e desenha telas. */
>>>>>>> 1c870c6 (test)
    private static class GamePanel extends JPanel implements ActionListener {
        private static final long serialVersionUID = 1L;

        private static final int WIDTH = 800;
        private static final int HEIGHT = 600;
        private static final int HUD_HEIGHT = 68;
        private static final int PLAY_HEIGHT = HEIGHT - HUD_HEIGHT;
        private static final int ARENA_LEFT = 54;
        private static final int ARENA_TOP = 74;
        private static final int ARENA_RIGHT = WIDTH - 54;
        private static final int ARENA_BOTTOM = PLAY_HEIGHT - 10;
        private static final double ARENA_CENTER_X = (ARENA_LEFT + ARENA_RIGHT) / 2.0;
        private static final double ARENA_CENTER_Y = (ARENA_TOP + ARENA_BOTTOM) / 2.0;
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
<<<<<<< HEAD
=======
        private final Map<Projectile, Fighter> projectileOwners = new IdentityHashMap<>();
>>>>>>> 1c870c6 (test)
        private final boolean[] keys = new boolean[512];
        private final boolean[] mouseButtons = new boolean[3];
        private Boss[] bosses = createBosses();
        private int bossIndex;
        private int fireTick;
<<<<<<< HEAD
=======
        private int playerOneBossDamage;
        private int playerTwoBossDamage;
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Define o tamanho da arena, carrega os fundos e o portal, registra os controles
         * e inicia o Timer. A cada 16 ms o Timer chama actionPerformed para avançar o jogo.
         */
>>>>>>> 1c870c6 (test)
        GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            background = loadBackground();
            menuBackground = loadMenuBackground();
            portal = new Portal(WIDTH / 2, PLAY_HEIGHT / 2, loadPortalImage());
            configureControls();
            timer.start();
        }

<<<<<<< HEAD
=======
        /**
         * Procura o fundo do menu primeiro como recurso empacotado; se não estiver no
         * classpath, tenta o caminho src usado durante o desenvolvimento. Retorna null
         * nos dois casos de falha para drawMenu pintar o gradiente de reserva.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Procura a imagem do portal no classpath e depois em src/image. Se nenhuma
         * leitura funcionar, retorna null; Portal.draw então desenha uma forma simples.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Abre um recurso de imagem pelo nome absoluto do classpath, como /image/background.png.
         * Trata recurso ausente e erro de leitura retornando null para o chamador decidir
         * qual imagem de reserva usar.
         */
>>>>>>> 1c870c6 (test)
        private BufferedImage loadImage(String resource) {
            try {
                return ImageIO.read(getClass().getResource(resource));
            } catch (IOException | IllegalArgumentException ex) {
                return null;
            }
        }

<<<<<<< HEAD
=======
        /**
         * Carrega a imagem de cenário usada por drawOverworld. Quando ela não existe,
         * informa o problema e retorna null para que drawOverworld desenhe o campo simples.
         */
>>>>>>> 1c870c6 (test)
        private BufferedImage loadBackground() {
            BufferedImage image = loadImage("/image/background.png");
            if (image == null) {
                System.err.println("Não foi possível carregar /image/background.png; usando fundo alternativo.");
            }
            return image;
        }

<<<<<<< HEAD
=======
        /**
         * Liga pressionar/soltar teclas a bind e instala listeners do mouse. Os listeners
         * iniciam o menu, ativam o portal no intervalo, guardam botões de tiro e atualizam
         * o efeito visual do botão START quando o ponteiro entra ou sai da área dele.
         */
>>>>>>> 1c870c6 (test)
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
<<<<<<< HEAD
=======
                /** Trata clique no botão do menu, clique para ativar portal e botões de tiro. */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
                /** Libera o estado do botão do mouse para que o jogador pare de atirar. */
>>>>>>> 1c870c6 (test)
                @Override
                public void mouseReleased(MouseEvent event) {
                    int button = event.getButton();
                    if (button >= 1 && button <= mouseButtons.length) {
                        mouseButtons[button - 1] = false;
                    }
                }

<<<<<<< HEAD
=======
                /** Remove o realce do botão START quando o ponteiro sai do painel. */
>>>>>>> 1c870c6 (test)
                @Override
                public void mouseExited(MouseEvent event) {
                    startButtonHovered = false;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseAdapter() {
<<<<<<< HEAD
=======
                /** Atualiza o realce do botão START e redesenha o menu sob o ponteiro. */
>>>>>>> 1c870c6 (test)
                @Override
                public void mouseMoved(MouseEvent event) {
                    startButtonHovered = screen == Screen.MENU && START_BUTTON.contains(event.getPoint());
                    repaint();
                }
            });
        }

<<<<<<< HEAD
=======
        /**
         * Registra no InputMap a tecla pressionada ou solta e cria sua ação no ActionMap.
         * A ação trata comandos especiais (iniciar, pausar, menu, HUD e portal); as teclas
         * de movimento e tiro atualizam diretamente o vetor keys, lido em cada quadro.
         * @param keyCode código KeyEvent da tecla
         * @param name identificador da ação Swing
         * @param pressed true para pressionamento, false para soltura
         */
>>>>>>> 1c870c6 (test)
        private void bind(int keyCode, String name, boolean pressed) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(keyCode, 0, !pressed), name);
            getActionMap().put(name, new AbstractAction() {
                private static final long serialVersionUID = 1L;

<<<<<<< HEAD
=======
                /** Encaminha comandos de tela para seus métodos e grava teclas de movimento/tiro. */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
                    int keyIndex = keyIndex(name.replace("Up", ""));
                    if (keyIndex >= 0) {
                        keys[keyIndex] = pressed;
                    }
=======
                    // O código da tecla já é o índice usado no vetor de entrada.
                    keys[keyCode] = pressed;
>>>>>>> 1c870c6 (test)
                }
            });
        }

<<<<<<< HEAD
        private int keyIndex(String key) {
            switch (key) {
                case "w": return KeyEvent.VK_W;
                case "a": return KeyEvent.VK_A;
                case "s": return KeyEvent.VK_S;
                case "d": return KeyEvent.VK_D;
                case "up": return KeyEvent.VK_UP;
                case "down": return KeyEvent.VK_DOWN;
                case "left": return KeyEvent.VK_LEFT;
                case "right": return KeyEvent.VK_RIGHT;
                case "space": return KeyEvent.VK_SPACE;
                default: return -1;
            }
        }

=======
        /**
         * Começa uma partida nova: delega a limpeza completa a resetMatch, muda a tela
         * para PLAYING, inicia a contagem de tempo e solicita um redesenho do painel.
         */
>>>>>>> 1c870c6 (test)
        private void startGame() {
            resetMatch();
            screen = Screen.PLAYING;
            runStartedAt = System.currentTimeMillis();
            runEndedAt = 0;
            repaint();
        }

<<<<<<< HEAD
=======
        /**
         * Descarta projéteis e explosões, restaura os dois jogadores, recria os chefes,
         * zera pontuação, combo, pausas e contadores de dano e limpa as entradas ativas.
         * startGame chama este método antes de iniciar o cronômetro da nova partida.
         */
>>>>>>> 1c870c6 (test)
        private void resetMatch() {
            playerOne.reset();
            playerTwo.reset();
            playerOneExplosion = null;
            playerTwoExplosion = null;
            projectiles.clear();
<<<<<<< HEAD
            Arrays.fill(keys, false);
            Arrays.fill(mouseButtons, false);
=======
            projectileOwners.clear();
            playerOneBossDamage = 0;
            playerTwoBossDamage = 0;
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /** Muda para MENU, limpa entradas presas e agenda o redesenho da tela inicial. */
>>>>>>> 1c870c6 (test)
        private void showMenu() {
            screen = Screen.MENU;
            clearInput();
            repaint();
        }

<<<<<<< HEAD
=======
        /** Zera os vetores de entrada; é usado ao reiniciar, pausar ou trocar de tela. */
>>>>>>> 1c870c6 (test)
        private void clearInput() {
            Arrays.fill(keys, false);
            Arrays.fill(mouseButtons, false);
        }

<<<<<<< HEAD
=======
        /**
         * Ao pausar PLAYING ou INTERMISSION, guarda a tela de origem e marca o instante
         * da pausa. Ao chamar novamente durante PAUSED, delega a retomada a resumeGame.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Soma o intervalo pausado ao total excluído do cronômetro, restaura pausedFrom,
         * limpa controles antigos e redesenha para continuar exatamente daquela tela.
         */
>>>>>>> 1c870c6 (test)
        private void resumeGame() {
            totalPausedMillis += System.currentTimeMillis() - pauseStartedAt;
            pauseStartedAt = 0;
            screen = pausedFrom;
            clearInput();
            repaint();
        }

<<<<<<< HEAD
=======
        /**
         * Retorna milissegundos desde runStartedAt, menos pausas acumuladas. Durante a
         * pausa usa pauseStartedAt; após vitória/derrota usa runEndedAt para congelar o tempo.
         */
>>>>>>> 1c870c6 (test)
        private long elapsedMillis() {
            if (runStartedAt == 0) {
                return 0;
            }
            long now = runEndedAt > 0 ? runEndedAt
                    : screen == Screen.PAUSED ? pauseStartedAt : System.currentTimeMillis();
            return Math.max(0, now - runStartedAt - totalPausedMillis);
        }

<<<<<<< HEAD
=======
        /**
         * Callback do Timer (aprox. 60 vezes por segundo). No intervalo atualiza jogadores,
         * portal e movimento; em PLAYING atualiza animações, entradas, chefe, ataques,
         * projéteis e transições. Em telas estáticas retorna sem alterar a partida.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Verifica a condição de derrota. Só troca para DEFEAT quando ambos estão sem vida
         * e as animações de explosão terminaram; retorna true para interromper o quadro atual.
         */
>>>>>>> 1c870c6 (test)
        private boolean checkDefeat() {
            if (playerOne.isDead() && playerTwo.isDead()
                    && playerOneExplosion == null && playerTwoExplosion == null) {
                runEndedAt = System.currentTimeMillis();
                screen = Screen.DEFEAT;
                return true;
            }
            return false;
        }

<<<<<<< HEAD
=======
        /**
         * Calcula a direção de cada jogador subtraindo tecla esquerda/cima da direita/baixo,
         * e envia direção, velocidade e limites da arena para Fighter.move.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /** Permite subtrair estados de teclas para formar uma direção -1, 0 ou 1. */
>>>>>>> 1c870c6 (test)
        private int pressed(int keyCode) {
            return keys[keyCode] ? 1 : 0;
        }

<<<<<<< HEAD
=======
        /**
         * Avança o contador de tiro e, a cada PLAYER_FIRE_INTERVAL quadros, consulta
         * espaço para o jogador 1 e o botão esquerdo do mouse para o jogador 2.
         */
>>>>>>> 1c870c6 (test)
        private void updatePlayerAttacks() {
            fireTick++;
            if (fireTick % PLAYER_FIRE_INTERVAL != 0) {
                return;
            }

            fire(playerOne, keys[KeyEvent.VK_SPACE]);
            fire(playerTwo, mouseButtons[0]);
        }

<<<<<<< HEAD
=======
        /** Se o botão de tiro está ativo, pede um projétil ao Fighter e registra quem o disparou. */
>>>>>>> 1c870c6 (test)
        private void fire(Fighter fighter, boolean fireButtonDown) {
            if (!fireButtonDown) {
                return;
            }
            Projectile projectile = fighter.fire();
            if (projectile != null) {
                projectiles.add(projectile);
<<<<<<< HEAD
            }
        }

=======
                projectileOwners.put(projectile, fighter);
            }
        }

        /**
         * Consulta o contrato BossBehavior do chefe e chama sua rotina própria com o
         * alvo escolhido; chefes sem esse contrato não executam ataques personalizados.
         */
>>>>>>> 1c870c6 (test)
        private void updateBossAttacks(Boss boss) {
            if (boss instanceof BossBehavior) {
                BossBehavior behavior = (BossBehavior) boss;
                behavior.updateBehavior(this, chooseTarget(boss.tick()));
            }
        }

<<<<<<< HEAD
=======
        /** Mede a distância de cada jogador até o chefe e aplica dano aos que estão no raio. */
>>>>>>> 1c870c6 (test)
        private void hitPlayersInRadius(Boss boss, double radius, boolean shieldPiercing) {
            if (Math.hypot(playerOne.centerX() - boss.x(), playerOne.centerY() - boss.y()) <= radius) {
                applyPlayerDamage(playerOne, shieldPiercing);
            }
            if (Math.hypot(playerTwo.centerX() - boss.x(), playerTwo.centerY() - boss.y()) <= radius) {
                applyPlayerDamage(playerTwo, shieldPiercing);
            }
        }

<<<<<<< HEAD
=======
        /**
         * Ignora impactos durante invulnerabilidade; caso contrário aplica dano, ativa
         * tremor de tela e cria uma explosão quando receiveHit confirma a morte.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Retorna o único jogador vivo, se houver. Com ambos vivos alterna o alvo a cada
         * 45 quadros; durante o terceiro chefe escolhe quem causou mais dano a ele.
         */
>>>>>>> 1c870c6 (test)
        private Fighter chooseTarget(int tick) {
            if (!playerOne.isAlive()) {
                return playerTwo;
            }
            if (!playerTwo.isAlive()) {
                return playerOne;
            }
<<<<<<< HEAD
            return (tick / 45) % 2 == 0 ? playerOne : playerTwo;
        }

=======
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
>>>>>>> 1c870c6 (test)
        private void shootFan(Boss boss, Fighter target, int count, double spread,
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

<<<<<<< HEAD
=======
        /**
         * Divide 2π por count, cria um projétil em cada ângulo e o adiciona à lista ativa.
         * É usado pelos chefes para ataques circulares independentes da posição dos jogadores.
         */
>>>>>>> 1c870c6 (test)
        private void shootRadial(Boss boss, int count, double speed, Color color) {
            for (int index = 0; index < count; index++) {
                double angle = Math.PI * 2 * index / count;
                projectiles.add(new Projectile(boss.x(), boss.y() + 25,
                        boss.x() + Math.cos(angle) * 500,
                        boss.y() + Math.sin(angle) * 500,
                        speed, true, color, 6));
            }
        }

<<<<<<< HEAD
        private void updateProjectiles(Boss boss) {
            Iterator<Projectile> iterator = projectiles.iterator();
            List<Projectile> splitProjectiles = new ArrayList<>();
=======
        /**
         * Percorre os projéteis com Iterator para poder removê-los com segurança. Atualiza
         * posição, elimina os que saíram da tela, troca projéteis divididos por oito novos,
         * aplica dano ao chefe ou aos jogadores e só depois acrescenta os novos projéteis.
         */
        private void updateProjectiles(Boss boss) {
            Iterator<Projectile> iterator = projectiles.iterator();
            // A lista temporária só é necessária nos quadros em que um projétil se divide.
            List<Projectile> splitProjectiles = null;
>>>>>>> 1c870c6 (test)

            while (iterator.hasNext()) {
                Projectile projectile = iterator.next();
                projectile.update();

                if (projectile.outside(WIDTH, HEIGHT)) {
<<<<<<< HEAD
=======
                    projectileOwners.remove(projectile);
>>>>>>> 1c870c6 (test)
                    iterator.remove();
                    continue;
                }

                if (projectile.consumeSplitReady()) {
<<<<<<< HEAD
=======
                    if (splitProjectiles == null) {
                        splitProjectiles = new ArrayList<>(8);
                    }
>>>>>>> 1c870c6 (test)
                    addSplitProjectiles(projectile, splitProjectiles);
                    iterator.remove();
                    continue;
                }

                if (!projectile.fromBoss() && !boss.defeated()
                        && Math.hypot(projectile.x() - boss.x(), projectile.y() - boss.y()) < 35) {
                    boss.damage();
<<<<<<< HEAD
=======
                    Fighter owner = projectileOwners.remove(projectile);
                    if (owner == playerOne) {
                        playerOneBossDamage++;
                    } else if (owner == playerTwo) {
                        playerTwoBossDamage++;
                    }
>>>>>>> 1c870c6 (test)
                    registerBossHit();
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

<<<<<<< HEAD
            projectiles.addAll(splitProjectiles);
        }

=======
            if (splitProjectiles != null) {
                projectiles.addAll(splitProjectiles);
            }
        }

        /** Registra o instante do acerto, inicia/continua o combo e soma pontos proporcionais a ele. */
>>>>>>> 1c870c6 (test)
        private void registerBossHit() {
            long now = System.currentTimeMillis();
            combo = now - lastComboAt <= 1800 ? combo + 1 : 1;
            lastComboAt = now;
            score += 10 + Math.min(combo, 20);
        }

<<<<<<< HEAD
=======
        /**
         * Cria oito trajetórias igualmente espaçadas a partir da posição do projétil original
         * e as guarda na lista temporária, evitando modificar projectiles durante sua iteração.
         */
>>>>>>> 1c870c6 (test)
        private void addSplitProjectiles(Projectile source, List<Projectile> destination) {
            for (int index = 0; index < 8; index++) {
                double angle = Math.PI * 2 * index / 8;
                destination.add(new Projectile(source.x(), source.y(),
                        source.x() + Math.cos(angle) * 100,
                        source.y() + Math.sin(angle) * 100,
                        3.8, true, new Color(255, 185, 70), 6));
            }
        }

<<<<<<< HEAD
=======
        /**
         * Compara a distância projétil-jogador com o raio de colisão. Retorna false sem alterar
         * o Iterator quando não há acerto; no acerto, aplica dano, remove o projétil e retorna true.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Não faz nada enquanto o chefe vive. Ao derrotá-lo, limpa projéteis e avança o índice;
         * registra vitória se era o último chefe ou muda para INTERMISSION e limpa controles.
         */
>>>>>>> 1c870c6 (test)
        private void advanceBoss(Boss boss) {
            if (!boss.defeated()) {
                return;
            }

            bossIndex++;
            projectiles.clear();
<<<<<<< HEAD
=======
            projectileOwners.clear();
            if (bossIndex == 2) {
                playerOneBossDamage = 0;
                playerTwoBossDamage = 0;
            }
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Valida tela e distância do jogador ao portal. Se ambas forem válidas, retoma PLAYING,
         * prepara o aviso com o nome do próximo chefe e limpa teclas para evitar movimento preso.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /** Atualiza cada explosão existente e apaga sua referência quando a animação termina. */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Pinta uma cópia do contexto gráfico: menu retorna após desenhar sua tela; nas demais
         * telas desenha cenário, portal/chefe, projéteis, jogadores, HUD, aviso e overlays nessa
         * ordem. A cópia é descartada ao final para não alterar o contexto do Swing.
         */
>>>>>>> 1c870c6 (test)
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
<<<<<<< HEAD
        if (isIntermissionScreen()) {
=======
            if (isIntermissionScreen()) {
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /** Decide se paintComponent deve desenhar o portal nesta tela ou numa pausa do intervalo. */
>>>>>>> 1c870c6 (test)
        private boolean isIntermissionScreen() {
            return screen == Screen.INTERMISSION
                    || (screen == Screen.PAUSED && pausedFrom == Screen.INTERMISSION);
        }

<<<<<<< HEAD
=======
        /**
         * Desenha o fundo do menu (imagem ou gradiente), aplica sombra para contraste,
         * renderiza título e botão. startButtonHovered escolhe as cores do gradiente;
         * o listener do mouse mantém esse estado atualizado.
         */
>>>>>>> 1c870c6 (test)
        private void drawMenu(Graphics2D g) {
            if (menuBackground != null) {
                drawCover(g, menuBackground);
            } else {
                g.setPaint(new GradientPaint(0, 0, new Color(12, 18, 48),
                        WIDTH, HEIGHT, new Color(90, 25, 75)));
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }

<<<<<<< HEAD
            g.setFont(new Font("SansSerif", Font.BOLD, 30));
            g.setColor(startButtonHovered ? new Color(100, 235, 255) : Color.WHITE);
            String label = "START";
            g.drawString(label, START_BUTTON.x + (START_BUTTON.width
                    - g.getFontMetrics().stringWidth(label)) / 2, START_BUTTON.y + 40);
        }

=======
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
>>>>>>> 1c870c6 (test)
        private void drawCover(Graphics2D g, BufferedImage image) {
            double scale = Math.max((double) WIDTH / image.getWidth(),
                    (double) HEIGHT / image.getHeight());
            int drawWidth = (int) Math.ceil(image.getWidth() * scale);
            int drawHeight = (int) Math.ceil(image.getHeight() * scale);
            int x = (WIDTH - drawWidth) / 2;
            int y = (HEIGHT - drawHeight) / 2;
            g.drawImage(image, x, y, drawWidth, drawHeight, null);
        }

<<<<<<< HEAD
=======
        /**
         * Se existe um chefe ativo, desenha no cabeçalho seu índice/nome, tempo decorrido,
         * pontuação e combo. O tempo passa por formatTime e os textos ficam alinhados às bordas.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /**
         * Desenha o aviso temporário de próximo chefe centralizado na parte superior da arena.
         * paintComponent chama este método enquanto o contador, reduzido em actionPerformed,
         * está ativo e a tela está em jogo. activatePortal define texto e duração; aqui a largura
         * da fonte calcula o X para centralizar a mensagem dentro do painel arredondado.
         */
>>>>>>> 1c870c6 (test)
        private void drawTransitionToast(Graphics2D g) {
            g.setColor(new Color(8, 15, 28, 205));
            g.fillRoundRect(184, 88, WIDTH - 368, 38, 14, 14);
            g.setColor(new Color(255, 225, 105));
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
<<<<<<< HEAD
            g.drawString(transitionToast, WIDTH / 2 - g.getFontMetrics().stringWidth(transitionToast) / 2, 113);
        }

=======
            int textX = (WIDTH - g.getFontMetrics().stringWidth(transitionToast)) / 2;
            g.drawString(transitionToast, textX, 113);
        }

        /**
         * paintComponent chama este método em pausa, vitória ou derrota. Ele escurece o quadro
         * já desenhado e centraliza o título recebido; só na vitória acrescenta tempo da partida
         * e melhor tempo da sessão. Não muda o estado do jogo, apenas desenha a camada superior.
         */
>>>>>>> 1c870c6 (test)
        private void drawOverlay(Graphics2D g, String title) {
            g.setColor(new Color(0, 0, 0, 190));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 38));
            g.drawString(title, WIDTH / 2 - g.getFontMetrics().stringWidth(title) / 2, HEIGHT / 2 - 10);
            if (screen == Screen.VICTORY) {
<<<<<<< HEAD
                String record = "TEMPO: " + formatTime(elapsedMillis())
                        + "  •  MELHOR: " + formatTime(bestClearTimeMillis);
                g.drawString(record, WIDTH / 2 - g.getFontMetrics().stringWidth(record) / 2,
                        HEIGHT / 2 + 54);
            }
        }

        private String formatTime(long millis) {
            long seconds = Math.max(0, millis) / 1000;
            return String.format("%02d:%02d", seconds / 60, seconds % 60);
        }

=======
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
>>>>>>> 1c870c6 (test)
        private void drawHud(Graphics2D g) {
            int top = HEIGHT - HUD_HEIGHT + 7;
            g.setColor(new Color(15, 24, 25, 225));
            g.fillRoundRect(16, top, WIDTH - 32, HUD_HEIGHT - 14, 18, 18);
            g.setColor(new Color(220, 230, 210, 120));
            g.drawRoundRect(16, top, WIDTH - 32, HUD_HEIGHT - 14, 18, 18);
            drawPlayerHud(g, 30, top + 19, "P1", playerOne);
            drawPlayerHud(g, 410, top + 19, "P2", playerTwo);
        }

<<<<<<< HEAD
=======
        /**
         * Usa os dados do Fighter para desenhar vidas, escudos disponíveis/em recarga e a
         * barra de stamina. x/y definem a origem do bloco; label identifica P1 ou P2.
         */
>>>>>>> 1c870c6 (test)
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
                g.fillRoundRect(blockX, y - 10, 11, 9, 4, 4);
            }

            g.setColor(new Color(235, 238, 228));
            g.drawString("ESCUDO", x + 152, y);
            for (int index = 0; index < Fighter.MAX_SHIELDS; index++) {
                int blockX = x + 195 + index * 14;
                g.setColor(new Color(45, 64, 67));
                g.fillRoundRect(blockX, y - 10, 10, 9, 4, 4);
                if (index < fighter.shields()) {
                    g.setColor(new Color(80, 220, 255));
                    g.fillRoundRect(blockX, y - 10, 10, 9, 4, 4);
                } else if (index == fighter.shields()) {
                    int progress = (int) (10 * fighter.shieldRegenerationProgress());
                    if (progress > 0) {
                        g.setColor(new Color(80, 220, 255, 190));
                        g.fillRoundRect(blockX, y - 10, progress, 9, 4, 4);
                    }
                }
            }

            g.setColor(new Color(235, 238, 228));
            g.drawString("STAMINA", x + 38, y + 25);
            int barX = x + 96;
            int barY = y + 15;
            int barWidth = 245;
            g.setColor(new Color(55, 65, 64));
            g.fillRoundRect(barX, barY, barWidth, 10, 8, 8);
            g.setColor(fighter.stamina() < 25 ? new Color(245, 175, 70) : fighter.color());
            g.fillRoundRect(barX, barY, (int) (barWidth * fighter.stamina() / 100.0), 10, 8, 8);
            g.setColor(new Color(255, 255, 255, 100));
            g.drawRoundRect(barX, barY, barWidth, 10, 8, 8);
        }

<<<<<<< HEAD
=======
        /**
         * Desenha a imagem de cenário em toda a janela; se o recurso não foi carregado,
         * monta um campo verde quadriculado simples para que a partida ainda seja jogável.
         */
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
=======
        /** Retorna os quatro chefes na ordem da partida; resetMatch cria novas instâncias. */
>>>>>>> 1c870c6 (test)
        private static Boss[] createBosses() {
            return new Boss[] {
                    new HerancaBoss(),
                    new PolimorfismoBoss(),
                    new EncapsulamentoBoss(),
                    new AbstracaoBoss()
            };
        }
    }

<<<<<<< HEAD
    /** Cada chefe define seu próprio padrão de combate por polimorfismo. */
    private interface BossBehavior {
        void updateBehavior(BulletHellGame.GamePanel game, Fighter target);
    }

=======
    /** Contrato que permite ao painel executar os ataques próprios de cada chefe. */
    private interface BossBehavior {
        /** Recebe o painel para criar ataques e o alvo selecionado para este quadro. */
        void updateBehavior(BulletHellGame.GamePanel game, Fighter target);
    }

    /** Chefe 1: alterna entre tiro que se divide e pequena rajada em leque. */
>>>>>>> 1c870c6 (test)
    private static class HerancaBoss extends Boss implements BossBehavior {
        private int attackFrame;
        private int volleyIndex;

<<<<<<< HEAD
=======
        /** Inicializa identidade, cor e total de vida do primeiro chefe. */
>>>>>>> 1c870c6 (test)
        HerancaBoss() {
            super("HERANÇA", "O Descendente", new Color(70, 190, 100), 18);
        }

<<<<<<< HEAD
=======
        /** Alterna os padrões de ataque após cada intervalo de 105 quadros. */
>>>>>>> 1c870c6 (test)
        @Override
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
            if (++attackFrame < 105) {
                return;
            }
            attackFrame = 0;
            volleyIndex++;
            if (volleyIndex % 2 == 1) {
                game.shootFan(this, target, 1, 0, true, false,
                        2.45, new Color(95, 220, 120));
            } else {
                game.shootFan(this, target, 3, 0.52, false, false,
                        2.65, new Color(95, 220, 120));
            }
        }
    }

<<<<<<< HEAD
    private static class PolimorfismoBoss extends Boss implements BossBehavior {
        private static final int VOLLEY_INTERVAL = 21;
        private static final double ORBIT_WIDTH = 92;
        private static final double ORBIT_HEIGHT = 48;
        private static final double MIN_MOVEMENT_SPEED = 0.003;
        private static final double MAX_MOVEMENT_SPEED = 0.075;

        private int frame;
        private double angle;
        private double positionX = GamePanel.ARENA_CENTER_X;
        private double positionY = GamePanel.ARENA_CENTER_Y;

=======
    /** Chefe 2: fica no centro e aumenta levemente os tiros abaixo de 75% de vida. */
    private static class PolimorfismoBoss extends Boss implements BossBehavior {
        private static final int VOLLEY_INTERVAL = 30;
        private int frame;

        /** Inicializa o chefe centralizado e seu relógio de ataques. */
>>>>>>> 1c870c6 (test)
        PolimorfismoBoss() {
            super("POLIMORFISMO", "O Mutante", new Color(230, 75, 75), 23);
        }

<<<<<<< HEAD
        /** Orbita o centro; a velocidade aumenta conforme a vida diminui. */
        @Override
        public void update(int width) {
            frame++;
            double lifeLostRatio = 1.0 - (double) hp() / maxHp();
            double movementSpeed = MIN_MOVEMENT_SPEED
                    + (MAX_MOVEMENT_SPEED - MIN_MOVEMENT_SPEED) * lifeLostRatio;
            angle += movementSpeed;

            double centerX = GamePanel.ARENA_CENTER_X;
            double centerY = GamePanel.ARENA_CENTER_Y;
            positionX = centerX + Math.sin(angle) * ORBIT_WIDTH;
            positionY = centerY + Math.sin(angle * 1.7) * ORBIT_HEIGHT;
        }

=======
        /** Avança o relógio próprio sem alterar a posição fixa no centro da arena. */
        @Override
        public void update(int width) {
            frame++;
        }

        /** Devolve o relógio local usado para sincronizar animação e rajadas. */
>>>>>>> 1c870c6 (test)
        @Override
        public int tick() {
            return frame;
        }

<<<<<<< HEAD
        @Override
        public double x() {
            return positionX;
        }

        @Override
        public double y() {
            return positionY;
        }

=======
        /** Fixa a coordenada horizontal do chefe no centro da arena. */
        @Override
        public double x() {
            return GamePanel.ARENA_CENTER_X;
        }

        /** Fixa a coordenada vertical do chefe no centro da arena. */
        @Override
        public double y() {
            return GamePanel.ARENA_CENTER_Y;
        }

        /** Desenha a forma animada, o nome e a barra de vida do chefe. */
>>>>>>> 1c870c6 (test)
        @Override
        public void draw(Graphics2D g) {
            int radius = 31;
            g.setColor(new Color(230, 75, 75));
            int shape = (frame / 12) % 3;
            if (shape == 0) {
                g.fillOval((int) x() - radius, (int) y() - radius,
                        radius * 2, radius * 2);
            } else if (shape == 1) {
                int[] xPoints = {(int) x(), (int) x() + radius, (int) x(), (int) x() - radius};
                int[] yPoints = {(int) y() - radius, (int) y(), (int) y() + radius, (int) y()};
                g.fillPolygon(xPoints, yPoints, 4);
            } else {
                g.fillRoundRect((int) x() - radius, (int) y() - radius,
                        radius * 2, radius * 2, 12, 12);
            }

            g.setColor(Color.WHITE);
            String label = concept();
            g.drawString(label, (int) x() - g.getFontMetrics().stringWidth(label) / 2,
                    (int) y() - 40);
            g.setColor(new Color(50, 20, 20));
            g.fillRect((int) x() - 40, (int) y() + 38, 80, 7);
            g.setColor(new Color(255, 220, 70));
            g.fillRect((int) x() - 40, (int) y() + 38, 80 * hp() / maxHp(), 7);
        }

<<<<<<< HEAD
        @Override
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
            if (frame % VOLLEY_INTERVAL == 0) {
                game.shootFan(this, target, 9, 1.35, false, true,
                        4.15, new Color(255, 75, 100));
            }
            if (frame > 0 && frame % (VOLLEY_INTERVAL * 3) == 0) {
                game.shootRadial(this, 12, 3.8, new Color(190, 75, 255));
=======
        /** Dispara o leque periódico e uma rajada radial menos frequente. */
        @Override
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
            double shotSpeed = (double) hp() / maxHp() <= 0.75 ? 2.8 : 2.4;
            if (frame % VOLLEY_INTERVAL == 0) {
                game.shootFan(this, target, 7, 1.15, false, true,
                        shotSpeed, new Color(255, 75, 100));
            }
            if (frame > 0 && frame % (VOLLEY_INTERVAL * 4) == 0) {
                game.shootRadial(this, 10, shotSpeed - 0.2, new Color(190, 75, 255));
>>>>>>> 1c870c6 (test)
            }
        }
    }

<<<<<<< HEAD
=======
    /** Chefe 3: persegue o jogador prioritário e prepara ataques de contato. */
>>>>>>> 1c870c6 (test)
    private static class EncapsulamentoBoss extends Boss implements BossBehavior {
        private static final int MELEE_RADIUS = 126;
        private static final int WINDUP_FRAMES = 24;
        private static final int COOLDOWN_FRAMES = 84;
        private int cooldown = COOLDOWN_FRAMES;
        private int windup;
<<<<<<< HEAD

=======
        private double positionX = GamePanel.ARENA_CENTER_X;
        private double positionY = GamePanel.ARENA_CENTER_Y;

        /** Inicializa o chefe perseguidor no centro e configura o primeiro ciclo de ataque. */
>>>>>>> 1c870c6 (test)
        EncapsulamentoBoss() {
            super("ENCAPSULAMENTO", "A Caixa Selada", new Color(125, 175, 255), 28);
        }

<<<<<<< HEAD
        @Override
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
=======
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
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
            double dx = target.centerX() - positionX;
            double dy = target.centerY() - positionY;
            double distance = Math.hypot(dx, dy);
            if (distance > 1) {
                double step = Math.min(1.35, distance);
                positionX = Math.max(GamePanel.ARENA_LEFT + 35,
                        Math.min(GamePanel.ARENA_RIGHT - 35, positionX + dx / distance * step));
                positionY = Math.max(GamePanel.ARENA_TOP + 35,
                        Math.min(GamePanel.ARENA_BOTTOM - 35, positionY + dy / distance * step));
            }
>>>>>>> 1c870c6 (test)
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

<<<<<<< HEAD
        @Override
        public void draw(Graphics2D g) {
            super.draw(g);
=======
        /** Destaca o chefe e mostra o alcance crescente enquanto prepara o golpe. */
        @Override
        public void draw(Graphics2D g) {
            int centerX = (int) x();
            int centerY = (int) y();
            g.setColor(new Color(35, 190, 255, 55));
            g.fillOval(centerX - 43, centerY - 43, 86, 86);
            g.setColor(new Color(95, 225, 255, 150));
            g.setStroke(new BasicStroke(3.0f));
            g.drawOval(centerX - 37, centerY - 37, 74, 74);
            super.draw(g);

            // Mantém um contorno contrastante mesmo quando o fundo é claro.
            g.setColor(new Color(235, 250, 255, 230));
            g.setStroke(new BasicStroke(2.0f));
            g.drawOval(centerX - 33, centerY - 33, 66, 66);
>>>>>>> 1c870c6 (test)
            if (windup <= 0) {
                return;
            }

            float progress = 1.0f - (float) windup / WINDUP_FRAMES;
            int radius = (int) (MELEE_RADIUS * (0.55 + 0.45 * progress));
            Stroke previousStroke = g.getStroke();
            g.setStroke(new BasicStroke(3.0f));
            g.setColor(new Color(115, 195, 255, 105 + (int) (110 * progress)));
            g.drawOval((int) x() - radius, (int) y() - radius,
                    radius * 2, radius * 2);
            g.setStroke(previousStroke);
        }
    }

<<<<<<< HEAD
    private static class AbstracaoBoss extends Boss implements BossBehavior {
        private int attackFrame;

=======
    /** Chefe 4: combina disparos direcionados ao alvo com rajadas radiais. */
    private static class AbstracaoBoss extends Boss implements BossBehavior {
        private int attackFrame;

        /** Inicializa identidade, aparência e vida do chefe final. */
>>>>>>> 1c870c6 (test)
        AbstracaoBoss() {
            super("ABSTRAÇÃO", "O Molde Antigo", new Color(235, 170, 55), 32);
        }

<<<<<<< HEAD
=======
        /** Emite o leque a cada 25 quadros e a rajada radial a cada 50. */
>>>>>>> 1c870c6 (test)
        @Override
        public void updateBehavior(BulletHellGame.GamePanel game, Fighter target) {
            attackFrame++;
            if (attackFrame % 25 == 0) {
                game.shootFan(this, target, 7, 1.05, false, false,
                        4.8, new Color(235, 170, 55));
            }
            if (attackFrame % 50 == 0) {
                game.shootRadial(this, 8, 4.25, new Color(255, 185, 70));
            }
        }
    }

<<<<<<< HEAD
    /** Portal azul usado para avançar entre os chefes. */
=======
    /** Portal azul que inicia o próximo chefe quando um jogador chega perto. */
>>>>>>> 1c870c6 (test)
    private static class Portal {
        private static final int DRAW_HEIGHT = 108;
        private static final double INTERACTION_RADIUS = 62;

        private final int centerX;
        private final int centerY;
        private final BufferedImage image;
        private final Rectangle visibleBounds;
        private int animationTick;

<<<<<<< HEAD
=======
        /** Guarda centro e imagem e calcula uma vez a área visível da imagem transparente. */
>>>>>>> 1c870c6 (test)
        Portal(int centerX, int centerY, BufferedImage image) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.image = image;
            visibleBounds = findVisibleBounds(image);
        }

<<<<<<< HEAD
=======
        /** Avança o relógio que controla a pulsação visual do portal. */
>>>>>>> 1c870c6 (test)
        void update() {
            animationTick++;
        }

<<<<<<< HEAD
=======
        /** Verifica se um jogador vivo está dentro do raio de interação do portal. */
>>>>>>> 1c870c6 (test)
        boolean isPlayerNear(Fighter fighter) {
            return fighter.isAlive()
                    && Math.hypot(fighter.centerX() - centerX, fighter.centerY() - centerY)
                    <= INTERACTION_RADIUS;
        }

<<<<<<< HEAD
=======
        /** Retorna true se pelo menos um jogador puder alcançar o portal. */
>>>>>>> 1c870c6 (test)
        boolean isPlayerNear(Fighter first, Fighter second) {
            return isPlayerNear(first) || isPlayerNear(second);
        }

<<<<<<< HEAD
=======
        /** Desenha o brilho pulsante e a imagem do portal, com forma vetorial de reserva. */
>>>>>>> 1c870c6 (test)
        void draw(Graphics2D g, boolean playerNearby) {
            float pulse = (float) ((Math.sin(animationTick * 0.09) + 1) / 2);
            int alpha = playerNearby ? 55 + (int) (75 * pulse) : 24 + (int) (40 * pulse);
            int glowWidth = playerNearby ? 92 : 78;
            g.setColor(new Color(40, 160, 255, alpha));
            g.fillOval(centerX - glowWidth / 2, centerY + 39, glowWidth,
                    playerNearby ? 26 : 20);

            if (image == null || visibleBounds == null) {
                g.setColor(new Color(35, 145, 255, 180));
                g.fillOval(centerX - 30, centerY - 50, 60, 100);
                g.setColor(new Color(125, 235, 255));
                g.drawOval(centerX - 30, centerY - 50, 60, 100);
                return;
            }

            int drawWidth = Math.max(1, (int) Math.round(
                    visibleBounds.width * DRAW_HEIGHT / (double) visibleBounds.height));
            int left = centerX - drawWidth / 2;
            int top = centerY - DRAW_HEIGHT / 2;
            g.drawImage(image, left, top, left + drawWidth, top + DRAW_HEIGHT,
                    visibleBounds.x, visibleBounds.y,
                    visibleBounds.x + visibleBounds.width,
                    visibleBounds.y + visibleBounds.height, null);
        }

<<<<<<< HEAD
=======
        /** Encontra a área não transparente para remover margens vazias ao desenhar o portal. */
        /**
         * Varre os pixels da imagem e encontra o retângulo cujo alpha é maior que 8.
         * Portal.draw usa essa área para dimensionar o desenho sem margens transparentes.
         */
>>>>>>> 1c870c6 (test)
        private Rectangle findVisibleBounds(BufferedImage source) {
            if (source == null) {
                return null;
            }
            int minX = source.getWidth();
            int minY = source.getHeight();
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    if (((source.getRGB(x, y) >>> 24) & 0xFF) > 8) {
                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxX < minX || maxY < minY) {
                return null;
            }
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }

<<<<<<< HEAD
    /** Estado e ações de um personagem, separado da coordenação da interface. */
=======
    /** Mantém estado, movimento, ataques e desenho de um jogador. */
>>>>>>> 1c870c6 (test)
    private static class Fighter {
        private static final int MAX_LIVES = 5;
        private static final int MAX_SHIELDS = 3;
        private static final int HIT_INVULNERABILITY_FRAMES = 48;
        private static final int SHIELD_RECHARGE_DELAY = 150;
        private static final int SHIELD_RECHARGE_INTERVAL = 120;
        private static final int PLAYER_SIZE = 22;
        private static final double MAX_STAMINA = 100;
        private static final double SHOT_STAMINA_COST = 4;

        private final Player character;
        private final int startX;
        private final int startY;
        private int lives = MAX_LIVES;
        private int shields = MAX_SHIELDS;
        private int invulnerabilityFrames;
        private int shieldRechargeFrames;
        private int shieldRechargePeriod;
        private double stamina = MAX_STAMINA;
        private double aimX;
        private double aimY = -1;
        private boolean rechargingStamina;

<<<<<<< HEAD
=======
        /** Cria o personagem visual e guarda sua posição inicial para reinícios. */
>>>>>>> 1c870c6 (test)
        Fighter(int x, int y, Color color) {
            character = new Player(x, y, color);
            startX = x;
            startY = y;
        }

<<<<<<< HEAD
=======
        /**
         * Chamado a cada quadro: reduz invulnerabilidade e delega recargas de escudo e stamina
         * aos métodos responsáveis por cada contador.
         */
>>>>>>> 1c870c6 (test)
        void update() {
            if (invulnerabilityFrames > 0) {
                invulnerabilityFrames--;
            }
            updateShieldRecharge();
            updateStaminaRecharge();
        }

<<<<<<< HEAD
=======
        /** Recarrega um escudo por vez após o atraso configurado desde o último golpe. */
>>>>>>> 1c870c6 (test)
        private void updateShieldRecharge() {
            if (isDead() || shields == MAX_SHIELDS || shieldRechargeFrames <= 0) {
                return;
            }

            shieldRechargeFrames--;
            if (shieldRechargeFrames == 0) {
                shields++;
                if (shields < MAX_SHIELDS) {
                    shieldRechargePeriod = SHIELD_RECHARGE_INTERVAL;
                    shieldRechargeFrames = SHIELD_RECHARGE_INTERVAL;
                } else {
                    shieldRechargePeriod = 0;
                }
            }
        }

<<<<<<< HEAD
=======
        /** Recupera stamina gradualmente após o jogador esgotar a barra ao atirar. */
>>>>>>> 1c870c6 (test)
        private void updateStaminaRecharge() {
            if (!rechargingStamina) {
                return;
            }
            stamina = Math.min(MAX_STAMINA, stamina + 0.65);
            if (stamina >= MAX_STAMINA) {
                rechargingStamina = false;
            }
        }

<<<<<<< HEAD
        void move(double dx, double dy, int speed,
                  int minX, int minY, int maxX, int maxY) {
            if (!isAlive()) {
                return;
            }
            if (dx != 0 || dy != 0) {
                double length = Math.hypot(dx, dy);
                aimX = dx / length;
                aimY = dy / length;
            }
            if (dx != 0 || dy != 0) {
                character.move(aimX * speed, aimY * speed,
                        minX, minY, maxX, maxY);
            }
        }

=======
        /** Normaliza a direção, atualiza a mira e move o personagem dentro dos limites. */
        void move(double dx, double dy, int speed,
                  int minX, int minY, int maxX, int maxY) {
            if (!isAlive() || (dx == 0 && dy == 0)) {
                return;
            }
            double length = Math.hypot(dx, dy);
            aimX = dx / length;
            aimY = dy / length;
            character.move(aimX * speed, aimY * speed, minX, minY, maxX, maxY);
        }

        /** Consome stamina e cria um tiro na direção da última mira; retorna null se indisponível. */
>>>>>>> 1c870c6 (test)
        Projectile fire() {
            if (!isAlive() || rechargingStamina || stamina < SHOT_STAMINA_COST) {
                return null;
            }

            stamina -= SHOT_STAMINA_COST;
            if (stamina <= 0) {
                stamina = 0;
                rechargingStamina = true;
            }
            return new Projectile(centerX(), centerY(),
                    centerX() + aimX * 500, centerY() + aimY * 500,
                    8.5, false, color(), 5);
        }

<<<<<<< HEAD
        /** Retorna true quando o golpe remove a última vida do personagem. */
=======
        /** Reduz escudo ou vida, inicia invulnerabilidade e informa se esse golpe foi fatal. */
>>>>>>> 1c870c6 (test)
        boolean receiveHit(boolean shieldPiercing) {
            if (!canReceiveHit()) {
                return false;
            }

            if (shields > 0 && !shieldPiercing) {
                shields--;
            } else {
                lives--;
            }
            invulnerabilityFrames = HIT_INVULNERABILITY_FRAMES;
            if (shields < MAX_SHIELDS) {
                shieldRechargePeriod = SHIELD_RECHARGE_DELAY;
                shieldRechargeFrames = SHIELD_RECHARGE_DELAY;
            }
            return isDead();
        }

<<<<<<< HEAD
=======
        /** Impede dano quando o jogador morreu ou ainda está invulnerável. */
>>>>>>> 1c870c6 (test)
        boolean canReceiveHit() {
            return isAlive() && invulnerabilityFrames == 0;
        }

<<<<<<< HEAD
=======
        /** Reposiciona o jogador e restaura vidas, escudos, mira e stamina. */
>>>>>>> 1c870c6 (test)
        void reset() {
            double currentX = centerX() - PLAYER_SIZE / 2.0;
            double currentY = centerY() - PLAYER_SIZE / 2.0;
            character.move(startX - currentX, startY - currentY,
                    GamePanel.ARENA_LEFT, GamePanel.ARENA_TOP,
                    GamePanel.ARENA_RIGHT, GamePanel.ARENA_BOTTOM);
            lives = MAX_LIVES;
            shields = MAX_SHIELDS;
            invulnerabilityFrames = 0;
            shieldRechargeFrames = 0;
            shieldRechargePeriod = 0;
            stamina = MAX_STAMINA;
            aimX = 0;
            aimY = -1;
            rechargingStamina = false;
        }

<<<<<<< HEAD
=======
        /** Desenha escudo e personagem, piscando enquanto o jogador está invulnerável. */
>>>>>>> 1c870c6 (test)
        void draw(Graphics2D g) {
            if (isDead() || (invulnerabilityFrames > 0 && (invulnerabilityFrames / 4) % 2 == 0)) {
                return;
            }

            Color originalColor = g.getColor();
            Stroke originalStroke = g.getStroke();
            if (shields > 0) {
                int alpha = 65 + shields * 45;
                g.setColor(new Color(80, 220, 255, alpha));
                g.setStroke(new BasicStroke(2.0f));
                g.drawOval((int) centerX() - 17, (int) centerY() - 17, 34, 34);
            }
            g.setStroke(originalStroke);
            g.setColor(originalColor);
            character.draw(g);
        }

<<<<<<< HEAD
        boolean isAlive() { return lives > 0; }
        boolean isDead() { return lives <= 0; }
        int lives() { return lives; }
        int shields() { return shields; }
        double stamina() { return stamina; }
        Color color() { return character.getColor(); }
        double centerX() { return character.centerX(); }
        double centerY() { return character.centerY(); }

=======
        /** Indica se ainda há pelo menos uma vida. */
        boolean isAlive() { return lives > 0; }
        /** Indica se as vidas chegaram a zero ou menos. */
        boolean isDead() { return lives <= 0; }
        /** Devolve vidas atuais para colisões e HUD. */
        int lives() { return lives; }
        /** Devolve escudos atuais para colisões e HUD. */
        int shields() { return shields; }
        /** Devolve stamina atual para o indicador inferior. */
        double stamina() { return stamina; }
        /** Devolve a cor do objeto Player para desenho e explosões. */
        Color color() { return character.getColor(); }
        /** Devolve a coordenada horizontal central do personagem. */
        double centerX() { return character.centerX(); }
        /** Devolve a coordenada vertical central do personagem. */
        double centerY() { return character.centerY(); }

        /** Calcula a fração de recarga do próximo escudo para preencher o indicador do HUD. */
>>>>>>> 1c870c6 (test)
        double shieldRegenerationProgress() {
            if (shields == MAX_SHIELDS || shieldRechargePeriod == 0) {
                return 0;
            }
            return Math.max(0, Math.min(1,
                    1.0 - (double) shieldRechargeFrames / shieldRechargePeriod));
        }
    }
}
