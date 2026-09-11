import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * Painel principal do jogo
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener {

    private enum Estado { TITULO, JOGANDO, PAUSADO, GAMEOVER }

    private final int LARGURA;
    private final int ALTURA;

    private Estado estado = Estado.TITULO;
    private Timer timer;
    private final Random rand = new Random();

    private LaserBot robo;
    private double mouseX, mouseY;

    private final List<Shot> tiros = new ArrayList<>();
    private final List<Enemy> inimigos = new ArrayList<>();
    private final List<FloatingText> textosFlutuantes = new ArrayList<>();
    private final List<Star> estrelas = new ArrayList<>();

    private boolean movendoEsquerda = false, movendoDireita = false;

    private final String nomePiloto = "MICHEL DE LIMA";

    // ---------- Créditos institucionais ----------
    private static final String UNIVERSIDADE = "Universidade Estadual de Ponta Grossa - PR";
    private static final String CURSO = "Computação Gráfica";
    private static final String ALUNO = "Aluno: Michel de Lima   RA: 21013723";
    private int score = 0;
    private int fase = 1;
    private int energia = 100;
    private int abates = 0;

    private int spawnTimer = 0;
    private int spawnIntervalo = 70;

    private Font fonteHUD;
    private Font fonteTitulo;

    public GamePanel(int largura, int altura) {
        this.LARGURA = largura;
        this.ALTURA = altura;

        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        mouseX = LARGURA / 2.0;
        mouseY = ALTURA / 2.0;

        robo = new LaserBot(LARGURA / 2.0, ALTURA - 60, LARGURA);

        carregarFontes();
        criarEstrelas();
    }

    private void carregarFontes() {
        Font carregada = null;
        try {
            File arquivoFonte = new File("game_font.ttf");
            if (arquivoFonte.exists()) {
                carregada = Font.createFont(Font.TRUETYPE_FONT, arquivoFonte);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(carregada);
            }
        } catch (FontFormatException | IOException e) {
            carregada = null;
        }
        Font base = (carregada != null) ? carregada : new Font("Monospaced", Font.BOLD, 12);
        fonteHUD = base.deriveFont(Font.BOLD, 16f);
        fonteTitulo = base.deriveFont(Font.BOLD, 50f);
    }

    private void criarEstrelas() {
        for (int i = 0; i < 120; i++) {
            estrelas.add(new Star(rand.nextInt(LARGURA), rand.nextInt(ALTURA), rand.nextInt(2) + 1));
        }
    }

    public void iniciarLoop() {
        timer = new Timer(16, this); // ~60 fps
        timer.start();
    }

    // ================= GAME LOOP =================
    @Override
    public void actionPerformed(ActionEvent e) {
        atualizar();
        repaint();
    }

    private void atualizar() {
        robo.mirar(mouseX, mouseY);

        if (estado == Estado.JOGANDO) {
            robo.mover(movendoEsquerda, movendoDireita);
            robo.atualizarRecuo();
            atualizarTiros();
            atualizarInimigos();
            atualizarTextosFlutuantes();
            gerenciarSpawn();
        }
        for (Star s : estrelas) s.atualizar(ALTURA);
    }

    private void atualizarTiros() {
        Iterator<Shot> it = tiros.iterator();
        while (it.hasNext()) {
            Shot t = it.next();
            t.atualizar();
            if (t.expirou()) { it.remove(); continue; }
            for (Enemy inimigo : inimigos) {
                if (!inimigo.isVivo()) continue;
                if (t.atingiu(inimigo.getBounds())) {
                    inimigo.destruir();
                    score += 100;
                    abates++;
                    textosFlutuantes.add(new FloatingText("+100 PTS",
                            inimigo.getX() + inimigo.getTamanho() / 2.0, inimigo.getY()));
                    if (abates % 10 == 0) {
                        fase++;
                        spawnIntervalo = Math.max(20, spawnIntervalo - 8);
                    }
                }
            }
        }
        inimigos.removeIf(en -> !en.isVivo());
    }

    private void atualizarInimigos() {
        double velocidade = 1.2 + fase * 0.35;
        Iterator<Enemy> it = inimigos.iterator();
        while (it.hasNext()) {
            Enemy en = it.next();
            en.atualizar(velocidade);
            if (en.saiuDaTela(ALTURA)) {
                it.remove();
                energia = Math.max(0, energia - 8);
                if (energia <= 0) estado = Estado.GAMEOVER;
            }
        }
    }

    private void atualizarTextosFlutuantes() {
        Iterator<FloatingText> it = textosFlutuantes.iterator();
        while (it.hasNext()) {
            FloatingText f = it.next();
            f.atualizar();
            if (f.terminou()) it.remove();
        }
    }

    private void gerenciarSpawn() {
        spawnTimer++;
        if (spawnTimer >= spawnIntervalo) {
            spawnTimer = 0;
            int x = 40 + rand.nextInt(LARGURA - 80);
            inimigos.add(new Enemy(x, -30, 28 + rand.nextInt(14)));
        }
    }

    private void atirar() {
        if (estado != Estado.JOGANDO) return;

        double px = robo.getPontaCanhaoX();
        double py = robo.getPontaCanhaoY();
        double dx = robo.getDirecaoX();
        double dy = robo.getDirecaoY();

        Point2D destino = calcularInterseccaoTela(px, py, dx, dy);

        List<Point> pontos = Bresenham.linha(
                (int) Math.round(px), (int) Math.round(py),
                (int) Math.round(destino.getX()), (int) Math.round(destino.getY()));

        tiros.add(new Shot(pontos, dx, dy));
        robo.ativarRecuo();
    }

    /** Estende a direção do disparo até a borda da tela (jogo estilo Defender). */
    private Point2D calcularInterseccaoTela(double x0, double y0, double dx, double dy) {
        double t = Double.MAX_VALUE;
        if (dx > 0) t = Math.min(t, (LARGURA - x0) / dx);
        else if (dx < 0) t = Math.min(t, (0 - x0) / dx);
        if (dy > 0) t = Math.min(t, (ALTURA - y0) / dy);
        else if (dy < 0) t = Math.min(t, (0 - y0) / dy);
        if (t == Double.MAX_VALUE || t < 0) t = 800;
        return new Point2D.Double(x0 + dx * t, y0 + dy * t);
    }

    private void reiniciar() {
        score = 0; fase = 1; energia = 100; abates = 0;
        spawnIntervalo = 70; spawnTimer = 0;
        robo = new LaserBot(LARGURA / 2.0, ALTURA - 60, LARGURA);
        tiros.clear();
        inimigos.clear();
        textosFlutuantes.clear();
        estado = Estado.JOGANDO;
    }

    // ================= DESENHO =================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharFundo(g2d);

        if (estado == Estado.TITULO) {
            desenharTitulo(g2d);
        } else if (estado == Estado.JOGANDO) {
            desenharJogo(g2d);
        } else if (estado == Estado.PAUSADO) {
            desenharJogo(g2d);
            desenharPause(g2d);
        } else if (estado == Estado.GAMEOVER) {
            desenharJogo(g2d);
            desenharGameOver(g2d);
        }
    }

    private void desenharFundo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, LARGURA, ALTURA);
        g2d.setColor(Color.WHITE);
        for (Star s : estrelas) s.desenhar(g2d);
        g2d.setColor(new Color(40, 60, 90));
        g2d.fillRect(0, ALTURA - 30, LARGURA, 30);
    }

    private void desenharJogo(Graphics2D g2d) {
        robo.desenhar(g2d);
        for (Shot t : tiros) t.desenhar(g2d);
        for (Enemy en : inimigos) en.desenhar(g2d);
        for (FloatingText f : textosFlutuantes) f.desenhar(g2d, fonteHUD);
        desenharHUD(g2d);
    }

    private void desenharHUD(Graphics2D g2d) {
        g2d.setFont(fonteHUD);
        FontMetrics fm = g2d.getFontMetrics();

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, LARGURA, 34);

        TextFX.desenharComSombra(g2d, "JOGADOR: " + nomePiloto, 15, 23, Color.CYAN);
        TextFX.desenharComSombra(g2d, "SCORE: " + score, 330, 23, Color.CYAN);
        TextFX.desenharComSombra(g2d, "FASE: " + fase, 520, 23, Color.CYAN);

        String barra = montarBarraEnergia();
        int largBarra = fm.stringWidth(barra); // Requisito 3: FontMetrics p/ alinhamento
        TextFX.desenharComSombra(g2d, barra, LARGURA - largBarra - 15, 23, Color.CYAN);
    }

    private String montarBarraEnergia() {
        int barras = energia / 10;
        StringBuilder sb = new StringBuilder("ENERGIA: [");
        for (int i = 0; i < 10; i++) sb.append(i < barras ? '=' : '-');
        sb.append("] ").append(energia).append('%');
        return sb.toString();
    }

    private void desenharTitulo(Graphics2D g2d) {
        TextFX.desenharCentralizadoComGradiente(g2d, "OPERAÇÃO RETROLASER", fonteTitulo,
                LARGURA, ALTURA / 2 - 60, new Color(0, 200, 255), new Color(255, 0, 130));

        TextFX.desenharInclinado(g2d, "Defenda a Estação Aegis-7", fonteHUD.deriveFont(22f),
                LARGURA, ALTURA / 2 + 10, Color.ORANGE);

        g2d.setFont(fonteHUD);
        FontMetrics fm = g2d.getFontMetrics();
        String msg = "Pressione ENTER para iniciar";
        long piscar = (System.currentTimeMillis() / 500) % 2;
        if (piscar == 0) {
            g2d.setColor(Color.WHITE);
            g2d.drawString(msg, (LARGURA - fm.stringWidth(msg)) / 2, ALTURA / 2 + 90);
        }

        g2d.setFont(fonteHUD.deriveFont(14f));
        g2d.setColor(Color.LIGHT_GRAY);
        String controles = "Setas/WASD: mover   Mouse: mirar   Clique/ESPAÇO: atirar   P: pausar";
        FontMetrics fm2 = g2d.getFontMetrics();
        g2d.drawString(controles, (LARGURA - fm2.stringWidth(controles)) / 2, ALTURA - 78);

        desenharCreditos(g2d, ALTURA - 50);
    }

    /** Créditos institucionais exibidos na tela de título. */
    private void desenharCreditos(Graphics2D g2d, int yBase) {
        g2d.setFont(fonteHUD.deriveFont(Font.BOLD, 15f));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(new Color(0, 200, 255));
        g2d.drawString(UNIVERSIDADE, (LARGURA - fm.stringWidth(UNIVERSIDADE)) / 2, yBase);

        g2d.setFont(fonteHUD.deriveFont(Font.PLAIN, 13f));
        FontMetrics fm2 = g2d.getFontMetrics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(CURSO, (LARGURA - fm2.stringWidth(CURSO)) / 2, yBase + 18);

        g2d.setColor(Color.WHITE);
        g2d.drawString(ALUNO, (LARGURA - fm2.stringWidth(ALUNO)) / 2, yBase + 36);
    }

    private void desenharPause(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, LARGURA, ALTURA);
        TextFX.desenharCentralizadoComGradiente(g2d, "PAUSE", fonteTitulo, LARGURA, ALTURA / 2,
                Color.WHITE, Color.GRAY);
    }

    private void desenharGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, LARGURA, ALTURA);
        TextFX.desenharCentralizadoComGradiente(g2d, "GAME OVER", fonteTitulo, LARGURA, ALTURA / 2 - 30,
                Color.RED, new Color(255, 180, 0));

        g2d.setFont(fonteHUD);
        FontMetrics fm = g2d.getFontMetrics();
        String msg1 = "Pontuação final: " + score;
        g2d.setColor(Color.WHITE);
        g2d.drawString(msg1, (LARGURA - fm.stringWidth(msg1)) / 2, ALTURA / 2 + 30);

        String msg2 = "Pressione ENTER para reiniciar";
        g2d.drawString(msg2, (LARGURA - fm.stringWidth(msg2)) / 2, ALTURA / 2 + 60);
    }

    // ================= ENTRADA (TECLADO / MOUSE) =================
    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) movendoEsquerda = true;
        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) movendoDireita = true;
        if (codigo == KeyEvent.VK_SPACE) atirar();
        if (codigo == KeyEvent.VK_ENTER) {
            if (estado == Estado.TITULO) estado = Estado.JOGANDO;
            else if (estado == Estado.GAMEOVER) reiniciar();
        }
        if (codigo == KeyEvent.VK_P) {
            if (estado == Estado.JOGANDO) estado = Estado.PAUSADO;
            else if (estado == Estado.PAUSADO) estado = Estado.JOGANDO;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) movendoEsquerda = false;
        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) movendoDireita = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }

    @Override
    public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }

    @Override
    public void mousePressed(MouseEvent e) { atirar(); }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
