package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


import model.*;

public class ScreenSaver extends JFrame {

    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int DELAY_MS = 16; 
    private static BufferedImage background;

    private ScreenSaverPanel canvas;

    public ScreenSaver() {
        super("Protetor de Tela - Demonstração de POO");

        this.canvas = new ScreenSaverPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setContentPane(canvas);

        // Usamos pack() para que a área útil desenhável seja rigorosamente 800x600,
        // sem ser reduzida pela borda ou barra de título da janela.
        this.pack();

        this.setLocationRelativeTo(null);
    }

    public void start() {
        this.setVisible(true);
        this.canvas.startAnimation();
        
        // Transfere o foco do teclado para o canvas assim que a janela abre
        this.canvas.requestFocusInWindow();
    }

    private class ScreenSaverPanel extends JPanel implements ActionListener {

        private Timer timer;

        /*********************************
        ** ↓ Declare suas formas aqui ↓ **
        *********************************/
        
        Player p1, p2;
        ArrayList<Projectile> projectiles;
        Boss boss;
        ArrayList<Player> players;
        PortalLevel portal;
        boolean reset;

        // Flags para rastrear teclas pressionadas
        private boolean space = false;
        private boolean up = false;
        private boolean down = false;
        private boolean right = false;
        private boolean left = false;
        private boolean shift = false;
        private boolean w = false;
        private boolean s = false;
        private boolean a = false;
        private boolean d = false;
        private boolean r = false;

        public ScreenSaverPanel() {
            this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
            this.setBackground(Color.BLACK);

            //Habilita o JPanel a receber foco para capturar eventos de teclado
            this.setFocusable(true);

            // Mapeamento de teclas
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Quando a tecla é pressionada, ativamos a flag
                    if (e.getKeyCode() == KeyEvent.VK_SPACE)
                        space = true;
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                        up = true;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        down = true;
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                        right = true;
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)
                        left = true;
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                        shift = true;
                    if (e.getKeyCode() == KeyEvent.VK_W)
                        w = true;
                    if (e.getKeyCode() == KeyEvent.VK_S)
                        s = true;
                    if (e.getKeyCode() == KeyEvent.VK_A)
                        a = true;
                    if (e.getKeyCode() == KeyEvent.VK_D)
                        d = true;
                    if (e.getKeyCode() == KeyEvent.VK_R)
                        r = true;
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Quando a tecla é solta, desativamos a flag
                    if (e.getKeyCode() == KeyEvent.VK_SPACE)
                        space = false;
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                        up = false;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        down = false;
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                        right = false;
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)
                        left = false;
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                        shift = false;
                    if (e.getKeyCode() == KeyEvent.VK_W)
                        w = false;
                    if (e.getKeyCode() == KeyEvent.VK_S)
                        s = false;
                    if (e.getKeyCode() == KeyEvent.VK_D)
                        d = false;
                    if (e.getKeyCode() == KeyEvent.VK_A)
                        a = false;
                    if (e.getKeyCode() == KeyEvent.VK_R)
                        r = false;
                }
            });

            /***********************************
            ** ↓ Instancie suas formas aqui ↓ **
            ***********************************/
            
            try {
                background = ImageIO.read(getClass().getResource("/image/background.png"));
            } catch (Exception e) {
                System.out.println("nun carrego o background");
            }

            p1 = new Player( 200, 450, new Color(255,0,0));
            p2 = new Player( 600, 450, new Color(0,0,255));
            players = new ArrayList<Player>();
            players.add(p1);
            players.add(p2);
            projectiles = new ArrayList<Projectile>();
            boss = new BossHeranca(300, 200);
            portal = new PortalLevel(-100, -100, null);
            reset = false;

            this.timer = new Timer(DELAY_MS, this);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
        }

        public void startAnimation() {
            this.timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /******************************
            ** ↓ Mova suas formas aqui ↓ **
            ******************************/
            //bordas: 50x, 75y, 700w, 450h
            if (w) {
                p1.move(0, -3, 50, 75, 750, 525);
            }
            if (a) {
                p1.move(-3, 0, 50, 75, 750, 525);
            }
            if (s) {
                p1.move(0, 3, 50, 75, 750, 525);
            }
            if (d) {
                p1.move(3, 0, 50, 75, 750, 525);
            }
            if (up) {
                p2.move(0, -3, 50, 75, 750, 525);
            }
            if (left) {
                p2.move(-3, 0, 50, 75, 750, 525);
            }
            if (down) {
                p2.move(0, 3, 50, 75, 750, 525);
            }
            if (right) {
                p2.move(3, 0, 50, 75, 750, 525);
            }
            if (shift) {
                if (p1.canShoot()) {
                    projectiles.add(p1.fire());
                }
            }
            // talvez troca sapoxa (a tecla espaço) por ponto e virgula 
            if (space) {
                if (p2.canShoot()) {
                    projectiles.add(p2.fire());
                }
            }
            if (r) {
                reset = true;
            }

            if (boss.shouldShoot()) {
                if (players.size() != 0) {
                    int target = (int) (Math.random() * players.size());
                    if (!players.get(target).isDead()) {
                        projectiles.add(boss.shoot(players.get(target)));
                    } else {
                        players.remove(target);
                    }
                }
            }

            boss.move(CANVAS_WIDTH);

            // movimenta os projéteis e checa colisão
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile proj = projectiles.get(i);
                proj.update(projectiles);
                if (boss.hit(proj) || p1.hit(proj) || p2.hit(proj) || 
                    proj.outside(CANVAS_WIDTH, CANVAS_HEIGHT)) {
                    projectiles.remove(i);
                    i--; // Ajusta o índice após a remoção
                }
            }

            

            if (boss.getHp() <= 0 ){
                portal.update();
                if (!portal.getVisible()) {
                    portal.changePos();
                }
                if (portal.isPlayerNear(p1, p2) && space) {
                    portal.changePos();
                    reset = true;
                }
            }

            if (reset) {
                p1 = new Player( 200, 450, new Color(255,0,0));
                p2 = new Player( 600, 450, new Color(0,0,255));
                boss = new BossHeranca(300, 200);
                projectiles.clear();
                portal = new PortalLevel(-100, -100, null);
                reset = false;
            }

            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            /*********************************
            ** ↓ Desenhe suas formas aqui ↓ **
            *********************************/

            //bordas: 50x, 75y, 750w, 525h
            g2d.drawImage(background, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, null);
            boss.draw(g2d);
            for (Projectile proj : projectiles) {
                proj.draw(g2d);
            }
            portal.draw(g2d, portal.isPlayerNear(p1, p2));
            p1.draw(g2d);
            p2.draw(g2d);
            



            /** Cria o fundo e a borda do HUD inferior e delega os indicadores a cada Fighter. */

            int HUD_HEIGHT = 50;
            int top = CANVAS_HEIGHT - HUD_HEIGHT + 7;
            g.setColor(new Color(15, 24, 25, 225));
            g.fillRoundRect(16, top, CANVAS_WIDTH - 32, HUD_HEIGHT - 14, 18, 18);
            g.setColor(new Color(220, 230, 210, 120));
            g.drawRoundRect(16, top, CANVAS_WIDTH - 32, HUD_HEIGHT - 14, 18, 18);
            p1.drawPlayerHud(g2d, 30, top + 19, "P1");
            p2.drawPlayerHud(g2d, 410, top + 19, "P2");
        }
    }
}