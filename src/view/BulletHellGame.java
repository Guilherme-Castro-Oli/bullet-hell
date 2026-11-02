package view;

import model.Boss;
import model.Explosion;
import model.Player;
import model.Projectile;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Janela do jogo. A interface original ScreenSaver.java não é usada nem alterada.
 * Esta classe cria uma tela própria para incluir mouse, HUD e regras de bullet hell.
 */
public class BulletHellGame extends JFrame {
    public BulletHellGame() {
        super("POO Hell — Arena dos Conceitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    /** Painel que recebe controles, atualiza o mundo e desenha cada quadro. */
    private static class GamePanel extends JPanel implements ActionListener {
        // Área desenhável e velocidade de caminhada (pixels por atualização).
        private static final int W = 800, H = 600, SPEED = 4;
        // Reserva a faixa inferior para o HUD e impede que os jogadores passem por baixo dele.
        private static final int HUD_HEIGHT = 68;
        private static final int PLAY_HEIGHT = H - HUD_HEIGHT;
        // O Timer chama actionPerformed aproximadamente a cada 16 ms (~60 quadros/s).
        private final Timer timer = new Timer(16, this);
        // A imagem fica carregada na memória; não é aberta de novo a cada quadro.
        private final BufferedImage background;
        private final Player p1 = new Player("Jogador 1", 280, 490, new Color(80, 220, 255));
        private final Player p2 = new Player("Jogador 2", 500, 490, new Color(255, 115, 180));
        private final List<Projectile> shots = new ArrayList<>();
        // Cada posição usa o código VK da tecla como índice: pressionada = true.
        private final boolean[] keys = new boolean[512];
        // Índices 0, 1 e 2 correspondem aos botões esquerdo, do meio e direito.
        private final boolean[] mouse = new boolean[3];
        // Os chefes ficam em sequência; bossIndex indica qual está ativo agora.
        private final Boss[] bosses = {
            new Boss("ABSTRAÇÃO", "O Molde Antigo", new Color(235, 170, 55), 18),
            new Boss("ENCAPSULAMENTO", "A Caixa Selada", new Color(175, 100, 235), 22),
            new Boss("HERANÇA", "O Descendente", new Color(70, 190, 100), 26),
            new Boss("POLIMORFISMO", "O Mutante", new Color(230, 75, 75), 32)
        };
        private int bossIndex = 0, p1Life = 5, p2Life = 5, fireTick = 0;
        // O efeito só é criado na última vida; o contador evita dano repetido entre golpes.
        private Explosion p1Explosion, p2Explosion;
        private int p1Invulnerability=0, p2Invulnerability=0;
        // Stamina vai de 0 a 100; a recuperação só começa depois que a barra zera.
        private double p1Stamina = 100, p2Stamina = 100;
        // A recarga é um estado: depois de zerar, só termina quando a barra enche.
        private boolean p1Recharging = false, p2Recharging = false;
        private boolean won = false, lost = false;
        // Última direção usada: permite continuar atirando para frente quando parado.
        private double p1FaceX = 0, p1FaceY = -1;
        private double p2FaceX = 0, p2FaceY = -1;

        GamePanel() {
            setPreferredSize(new Dimension(W, H));
            setFocusable(true);
            // Caminho relativo à raiz do classpath. No projeto, o arquivo está em src/image.
            BufferedImage loadedBackground = null;
            try {
                loadedBackground = ImageIO.read(getClass().getResource("/image/background.png"));
            } catch (IOException | IllegalArgumentException ex) {
                // Se a imagem não puder ser carregada, o desenho simples abaixo serve de reserva.
                System.err.println("Não foi possível carregar /image/background.png: " + ex.getMessage());
            }
            background = loadedBackground;
            // Key bindings registram ações para pressionar e soltar cada tecla.
            // WHEN_IN_FOCUSED_WINDOW ajuda a manter o controle ativo dentro da janela.
            bind(KeyEvent.VK_W, "w", true); bind(KeyEvent.VK_A, "a", true);
            bind(KeyEvent.VK_S, "s", true); bind(KeyEvent.VK_D, "d", true);
            bind(KeyEvent.VK_UP, "up", true); bind(KeyEvent.VK_DOWN, "down", true);
            bind(KeyEvent.VK_LEFT, "left", true); bind(KeyEvent.VK_RIGHT, "right", true);
            bind(KeyEvent.VK_SPACE, "space", true); bind(KeyEvent.VK_R, "restart", true);
            bind(KeyEvent.VK_W, "wUp", false); bind(KeyEvent.VK_A, "aUp", false);
            bind(KeyEvent.VK_S, "sUp", false); bind(KeyEvent.VK_D, "dUp", false);
            bind(KeyEvent.VK_UP, "upUp", false); bind(KeyEvent.VK_DOWN, "downUp", false);
            bind(KeyEvent.VK_LEFT, "leftUp", false); bind(KeyEvent.VK_RIGHT, "rightUp", false);
            bind(KeyEvent.VK_SPACE, "spaceUp", false);
            // O tiro só é contínuo enquanto o botão esquerdo estiver pressionado.
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { if(e.getButton()<=3) mouse[e.getButton()-1]=true; }
                public void mouseReleased(MouseEvent e) { if(e.getButton()<=3) mouse[e.getButton()-1]=false; }
            });
            timer.start();
        }

        private void bind(int key, String name, boolean pressed) {
            // O terceiro argumento de KeyStroke indica se a ação acontece ao soltar.
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0, !pressed), name);
            getActionMap().put(name, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (name.equals("restart")) { if (won || lost) restart(); return; }
                    String k = name.replace("Up", "");
                    int index = keyIndex(k);
                    if (index >= 0) keys[index] = pressed;
                }
            });
        }
        private int keyIndex(String k) {
            switch(k) { case "w":return KeyEvent.VK_W; case "a":return KeyEvent.VK_A; case "s":return KeyEvent.VK_S; case "d":return KeyEvent.VK_D;
                case "up":return KeyEvent.VK_UP; case "down":return KeyEvent.VK_DOWN; case "left":return KeyEvent.VK_LEFT; case "right":return KeyEvent.VK_RIGHT; case "space":return KeyEvent.VK_SPACE; default:return -1; }
        }
        private void restart() {
            bossIndex=0; p1Life=5; p2Life=5; p1Stamina=100; p2Stamina=100;
            p1Recharging=false; p2Recharging=false;
            p1Explosion=null; p2Explosion=null;
            p1Invulnerability=0; p2Invulnerability=0;
            won=false; lost=false; shots.clear();
            // Recria os chefes porque derrotados têm vida zerada e tick avançado.
            Boss[] fresh={new Boss("ABSTRAÇÃO","O Molde Antigo",new Color(235,170,55),18),new Boss("ENCAPSULAMENTO","A Caixa Selada",new Color(175,100,235),22),new Boss("HERANÇA","O Descendente",new Color(70,190,100),26),new Boss("POLIMORFISMO","O Mutante",new Color(230,75,75),32)};
            System.arraycopy(fresh,0,bosses,0,4);
        }

        public void actionPerformed(ActionEvent e) {
            // Este método é o ciclo principal: lê entradas, atualiza regras e redesenha.
            if (won || lost) { repaint(); return; }
            updateExplosions();
            if (p1Invulnerability>0) p1Invulnerability--;
            if (p2Invulnerability>0) p2Invulnerability--;
            // Só mostra a tela de derrota depois que as explosões finais terminarem.
            if (p1Life<=0 && p2Life<=0 && p1Explosion==null && p2Explosion==null) {
                lost=true;
                repaint();
                return;
            }
            // Converte teclas em direção: direita menos esquerda, baixo menos cima.
            double d1x=(keys[KeyEvent.VK_D]?1:0)-(keys[KeyEvent.VK_A]?1:0);
            double d1y=(keys[KeyEvent.VK_S]?1:0)-(keys[KeyEvent.VK_W]?1:0);
            double d2x=(keys[KeyEvent.VK_RIGHT]?1:0)-(keys[KeyEvent.VK_LEFT]?1:0);
            double d2y=(keys[KeyEvent.VK_DOWN]?1:0)-(keys[KeyEvent.VK_UP]?1:0);
            // Atualiza a direção para onde cada personagem está olhando ao se mover.
            // Se não houver movimento, conserva a última direção registrada.
            if (p1Life>0 && p1Explosion==null && (d1x != 0 || d1y != 0)) { double n=Math.hypot(d1x,d1y); p1FaceX=d1x/n; p1FaceY=d1y/n; }
            if (p2Life>0 && p2Explosion==null && (d2x != 0 || d2y != 0)) { double n=Math.hypot(d2x,d2y); p2FaceX=d2x/n; p2FaceY=d2y/n; }
            // Normalizar a diagonal evita que andar em diagonal seja mais rápido.
            if (p1Life>0 && p1Explosion==null) moveNormalized(p1,d1x,d1y);
            if (p2Life>0 && p2Explosion==null) moveNormalized(p2,d2x,d2y);
            Boss boss=bosses[bossIndex]; boss.update(W);
            // fireTick limita a frequência dos tiros para não criar um por quadro.
            fireTick++;
            if (p1Life>0 && p1Explosion==null && keys[KeyEvent.VK_SPACE] && fireTick%10==0 && !p1Recharging && p1Stamina>=4) {
                shots.add(new Projectile(p1.centerX(),p1.centerY(),p1.centerX()+p1FaceX*500,p1.centerY()+p1FaceY*500,8,false,p1.getColor(),5));
                p1Stamina=Math.max(0,p1Stamina-4);
                if (p1Stamina==0) p1Recharging=true;
            }
            if (p2Life>0 && p2Explosion==null && mouse[0] && fireTick%10==0 && !p2Recharging && p2Stamina>=4) {
                shots.add(new Projectile(p2.centerX(),p2.centerY(),p2.centerX()+p2FaceX*500,p2.centerY()+p2FaceY*500,8,false,p2.getColor(),5));
                p2Stamina=Math.max(0,p2Stamina-4);
                if (p2Stamina==0) p2Recharging=true;
            }
            // Só recarrega depois de zerar. Durante a recarga, aquele jogador não atira.
            if (p1Recharging) {
                p1Stamina=Math.min(100,p1Stamina+0.38);
                if (p1Stamina>=100) p1Recharging=false;
            }
            if (p2Recharging) {
                p2Stamina=Math.min(100,p2Stamina+0.38);
                if (p2Stamina>=100) p2Recharging=false;
            }
            // Chefes posteriores disparam em intervalos menores e ficam mais difíceis.
            if (boss.shouldShoot(Math.max(26,55-bossIndex*5))) {
                // Alvos alternados para que os dois jogadores precisem desviar.
                Player target=(boss.tick()/55)%2==0?p1:p2;
                if (bossIndex == 2) {
                    // HERANÇA lança um projétil que se divide em oito ao avançar.
                    shootFan(target, 1, 0, true, false, 3.6, new Color(255,145,45));
                } else if (bossIndex == 3) {
                    // POLIMORFISMO lança uma salva cujos projéteis alternam real/falso.
                    shootFan(target, 5, 0.75, false, true, 3.4, new Color(255,75,65));
                } else {
                    shootFan(target, 1, 0, false, false, 3.2+bossIndex*.35, new Color(255,90,65));
                }
            }
            updateShots(boss);
            if (boss.defeated()) { bossIndex++; shots.clear(); if(bossIndex==bosses.length) won=true; }
            repaint();
        }

        /** Avança cada explosão e remove o efeito quando as partículas desaparecem. */
        private void updateExplosions() {
            if (p1Explosion!=null) {
                p1Explosion.update();
                if (p1Explosion.isFinished()) p1Explosion=null;
            }
            if (p2Explosion!=null) {
                p2Explosion.update();
                if (p2Explosion.isFinished()) p2Explosion=null;
            }
        }
        private void moveNormalized(Player p,double dx,double dy) {
            // O vetor de movimento é dividido pelo seu comprimento antes de aplicar SPEED.
            double len=Math.hypot(dx,dy); if(len>0) p.move(dx/len*SPEED,dy/len*SPEED,W,PLAY_HEIGHT);
        }
        /** Cria uma salva com vários ângulos ao redor da direção do jogador-alvo. */
        private void shootFan(Player target, int count, double spread,
                              boolean splits, boolean changesTruth, double speed, Color color) {
            double dx=target.centerX()-bosses[bossIndex].x();
            double dy=target.centerY()-bosses[bossIndex].y();
            double baseAngle=Math.atan2(dy,dx);
            for (int i=0; i<count; i++) {
                // Distribui os tiros igualmente entre -spread/2 e +spread/2.
                double offset=count==1 ? 0 : -spread/2 + spread*i/(count-1.0);
                double angle=baseAngle+offset;
                double targetX=bosses[bossIndex].x()+Math.cos(angle)*500;
                double targetY=bosses[bossIndex].y()+Math.sin(angle)*500;
                shots.add(new Projectile(bosses[bossIndex].x(),bosses[bossIndex].y()+25,
                        targetX,targetY,speed,true,color,7,splits,changesTruth));
            }
        }
        private void updateShots(Boss boss) {
            // Iterator permite remover projéteis com segurança durante a passagem na lista.
            Iterator<Projectile> it=shots.iterator();
            List<Projectile> splitShots=new ArrayList<>();
            while(it.hasNext()) {
                Projectile s=it.next(); s.update();
                if(s.outside(W,H)) { it.remove(); continue; }
                // Os oito novos tiros são guardados à parte para não modificar a lista
                // enquanto o Iterator está percorrendo-a.
                if (s.consumeSplitReady()) {
                    for (int i=0; i<8; i++) {
                        double angle=Math.PI*2*i/8;
                        splitShots.add(new Projectile(s.x(),s.y(),s.x()+Math.cos(angle)*100,
                                s.y()+Math.sin(angle)*100,3.3,true,new Color(255,185,70),6));
                    }
                    it.remove();
                    continue;
                }
                // Distância entre os centros: se for menor que o raio aproximado,
                // o tiro do jogador acertou o chefe e é consumido.
                if(!s.fromBoss() && Math.hypot(s.x()-boss.x(),s.y()-boss.y())<35) { boss.damage(); it.remove(); continue; }
                // Tiros falsos do chefe de POLIMORFISMO passam sem causar dano.
                if(s.fromBoss() && s.isHarmful()) {
                    if(p1Life>0 && p1Invulnerability==0 && Math.hypot(s.x()-p1.centerX(),s.y()-p1.centerY())<18) {
                        p1Life--;
                        p1Invulnerability=45;
                        // Só a perda da última vida cria o efeito de explosão.
                        if(p1Life==0) p1Explosion=new Explosion(p1.centerX(),p1.centerY(),p1.getColor());
                        it.remove();
                    } else if(p2Life>0 && p2Invulnerability==0 && Math.hypot(s.x()-p2.centerX(),s.y()-p2.centerY())<18) {
                        p2Life--;
                        p2Invulnerability=45;
                        if(p2Life==0) p2Explosion=new Explosion(p2.centerX(),p2.centerY(),p2.getColor());
                        it.remove();
                    }
                }
            }
            shots.addAll(splitShots);
        }
        /** Swing chama este método quando precisa redesenhar o painel. */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            drawOverworld(g2);
            if (bossIndex<bosses.length) bosses[bossIndex].draw(g2);
            for(Projectile s:shots) s.draw(g2);
            // Mostra as partículas no lugar do quadrado durante a explosão.
            if (p1Explosion!=null) p1Explosion.draw(g2);
            else if (p1Life>0) p1.draw(g2);
            if (p2Explosion!=null) p2Explosion.draw(g2);
            else if (p2Life>0) p2.draw(g2);
            // As informações ficam no HUD inferior, sem texto sobre o topo do cenário.
            drawHud(g2);
            if(won||lost) {
                g2.setColor(new Color(0,0,0,190)); g2.fillRect(0,0,W,H);
                g2.setColor(Color.WHITE); g2.setFont(new Font("SansSerif",Font.BOLD,30));
                String message=won?"O programa foi salvo!":"Os Virus venceram!";
                g2.drawString(message, W/2-g2.getFontMetrics().stringWidth(message)/2, H/2);
                g2.setFont(new Font("SansSerif",Font.PLAIN,17)); g2.drawString("Pressione R para jogar novamente",285,H/2+36);
            }
            g2.dispose();
        }
        /** HUD inferior com indicadores de vida e barras de stamina dos jogadores. */
        private void drawHud(Graphics2D g) {
            int top=H-HUD_HEIGHT+7;
            // Fundo escuro translúcido separa as informações do cenário.
            g.setColor(new Color(15,24,25,225));
            g.fillRoundRect(16,top,W-32,HUD_HEIGHT-14,18,18);
            g.setColor(new Color(220,230,210,120));
            g.drawRoundRect(16,top,W-32,HUD_HEIGHT-14,18,18);
            drawPlayerHud(g,30,top+19,"P1",p1Life,p1Stamina,p1.getColor());
            drawPlayerHud(g,410,top+19,"P2",p2Life,p2Stamina,p2.getColor());
        }

        /** Desenha a identificação, as vidas e a barra contínua de stamina. */
        private void drawPlayerHud(Graphics2D g,int x,int y,String label,int life,
                                   double stamina,Color playerColor) {
            g.setFont(new Font("SansSerif",Font.BOLD,13));
            g.setColor(playerColor);
            g.drawString(label,x,y);
            g.setFont(new Font("SansSerif",Font.PLAIN,10));
            g.setColor(new Color(235,238,228));
            g.drawString("VIDA",x+38,y);
            for(int i=0;i<5;i++) {
                int blockX=x+72+i*15;
                g.setColor(i<life ? playerColor : new Color(75,82,80));
                g.fillRoundRect(blockX,y-10,11,9,4,4);
            }
            g.setColor(new Color(235,238,228));
            g.drawString("STAMINA",x+38,y+25);
            int barX=x+96, barY=y+15, barWidth=245;
            g.setColor(new Color(55,65,64));
            g.fillRoundRect(barX,barY,barWidth,10,8,8);
            g.setColor(stamina<25 ? new Color(245,175,70) : playerColor);
            g.fillRoundRect(barX,barY,(int)(barWidth*stamina/100.0),10,8,8);
            g.setColor(new Color(255,255,255,100));
            g.drawRoundRect(barX,barY,barWidth,10,8,8);
        }
        // Desenha a imagem background.png cobrindo a área do jogo.
        // O tamanho original pode ser diferente de 800x600, por isso ela é redimensionada.
        private void drawOverworld(Graphics2D g) {
            if (background != null) {
                g.drawImage(background, 0, 0, W, H, null);
                return;
            }
            // Fundo de reserva usado apenas se o recurso não estiver no classpath.
            g.setColor(new Color(91,143,57)); g.fillRect(0,0,W,H);
            g.setColor(new Color(102,157,62));
            for(int y=45;y<H;y+=48) for(int x=((y/48)%2)*24;x<W;x+=48) g.fillRect(x,y,24,24);
            g.setColor(new Color(225,210,145)); g.fillRect(0,38,W,3);
        }
    }
}
