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
        
        Player p1;


        

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

            p1 = new Player( 100, 100, new Color(255,0,0));


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
            p1.draw(g2d);


        }
    }
}