import java.awt.*;
import java.awt.geom.*;

/**
 * Requisito 1 - O Protagonista (LaserBot).
 *   - Translação: movimento horizontal via teclado (mover()).
 *   - Rotação: o canhão aponta para o mouse em tempo real (mirar()).
 *   - Escala: efeito de recuo ao disparar (ativarRecuo() / atualizarRecuo()).
 */
public class LaserBot {

    private double x;
    private final double feetY;
    private final double velocidade = 5.5;
    private final int limiteEsquerda;
    private final int limiteDireita;

    private double anguloCanhao = 0;
    private double escalaRecuo = 1.0;
    private int recuoTimer = 0;

    private static final double PIVO_OFFSET_X = 50;
    private static final double PIVO_OFFSET_Y = -160;
    private static final double COMPRIMENTO_CANO = 55;
    private static final int DURACAO_RECUO = 8;

    public LaserBot(double xInicial, double feetY, int larguraTela) {
        this.x = xInicial;
        this.feetY = feetY;
        this.limiteEsquerda = 70;
        this.limiteDireita = larguraTela - 70;
    }

    // ---------- Translação ----------
    public void mover(boolean esquerda, boolean direita) {
        if (esquerda) x -= velocidade;
        if (direita) x += velocidade;
        x = Math.max(limiteEsquerda, Math.min(limiteDireita, x));
    }

    // ---------- Rotação do canhão em direção ao mouse ----------
    public void mirar(double mouseX, double mouseY) {
        double px = getPivoCanhaoX();
        double py = getPivoCanhaoY();
        anguloCanhao = Math.atan2(mouseY - py, mouseX - px);
    }

    // ---------- Escala (efeito de recuo ao atirar) ----------
    public void ativarRecuo() {
        recuoTimer = DURACAO_RECUO;
    }

    public void atualizarRecuo() {
        if (recuoTimer > 0) {
            recuoTimer--;
            escalaRecuo = 1.0 + 0.18 * (recuoTimer / (double) DURACAO_RECUO);
        } else {
            escalaRecuo = 1.0;
        }
    }

    public double getPivoCanhaoX() { return x + PIVO_OFFSET_X; }
    public double getPivoCanhaoY() { return feetY + PIVO_OFFSET_Y; }

    public double getPontaCanhaoX() { return getPivoCanhaoX() + COMPRIMENTO_CANO * Math.cos(anguloCanhao); }
    public double getPontaCanhaoY() { return getPivoCanhaoY() + COMPRIMENTO_CANO * Math.sin(anguloCanhao); }

    public double getDirecaoX() { return Math.cos(anguloCanhao); }
    public double getDirecaoY() { return Math.sin(anguloCanhao); }

    public Rectangle getBounds() {
        return new Rectangle((int) (x - 55), (int) (feetY - 265), 110, 265);
    }

    // ================= DESENHO =================
    public void desenhar(Graphics2D g2d) {
        AffineTransform transformOriginal = g2d.getTransform();

        g2d.translate(x, feetY);        // Requisito 1: Translação
        g2d.scale(escalaRecuo, escalaRecuo); // Requisito 1: Escala (recuo)

        desenharCorpo(g2d);

        g2d.setTransform(transformOriginal);

        desenharCanhao(g2d);            // Requisito 1: Rotação (separada do corpo)
    }

    private void desenharCorpo(Graphics2D g2d) {
        GeneralPath contorno = construirContorno();
        double t = System.currentTimeMillis() / 1000.0; // tempo p/ animações de brilho

        g2d.setStroke(new BasicStroke(2.0f));

        // ---------- Pernas (com sombreamento em degradê) ----------
        GradientPaint pernaGrad = new GradientPaint(0, -60, new Color(90, 90, 100), 0, 0, new Color(45, 45, 55));
        g2d.setPaint(pernaGrad);
        g2d.fill(new Rectangle2D.Double(-35, -60, 20, 60)); // perna esquerda
        g2d.fill(new Rectangle2D.Double(15, -60, 20, 60));  // perna direita

        // ---------- Botas ----------
        g2d.setColor(new Color(25, 25, 30));
        g2d.fill(new RoundRectangle2D.Double(-40, -10, 28, 10, 4, 4)); // bota esquerda
        g2d.fill(new RoundRectangle2D.Double(12, -10, 28, 10, 4, 4));  // bota direita

        // ---------- Corpo (degradê diagonal metálico azul) ----------
        GradientPaint corpoGrad = new GradientPaint(-50, -180, new Color(170, 220, 245),
                50, -60, new Color(70, 120, 165));
        g2d.setPaint(corpoGrad);
        g2d.fill(new RoundRectangle2D.Double(-50, -180, 100, 120, 10, 10));

        // linhas de painel (textura mecânica)
        g2d.setColor(new Color(40, 70, 100));
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.draw(new Line2D.Double(-50, -150, 50, -150));
        g2d.draw(new Line2D.Double(-50, -120, 50, -120));
        g2d.draw(new Line2D.Double(-30, -180, -30, -60));
        g2d.draw(new Line2D.Double(30, -180, 30, -60));

        // ---------- Ombreiras ----------
        GradientPaint ombreiraGrad = new GradientPaint(0, -170, new Color(220, 60, 60), 0, -130, new Color(140, 20, 20));
        g2d.setPaint(ombreiraGrad);
        g2d.fill(new RoundRectangle2D.Double(-68, -172, 26, 32, 8, 8));
        g2d.fill(new RoundRectangle2D.Double(42, -172, 26, 32, 8, 8));

        // ---------- Núcleo do reator (peito) - brilho pulsante ----------
        double pulso = 0.5 + 0.5 * Math.sin(t * 4);
        float raioNucleo = (float) (13 + pulso * 3);
        Color corNucleo = new Color(120 + (int) (pulso * 100), 220, 255);
        RadialGradientPaint nucleoGrad = new RadialGradientPaint(
                new Point2D.Double(0, -130), raioNucleo,
                new float[] { 0f, 1f },
                new Color[] { Color.WHITE, new Color(corNucleo.getRed(), corNucleo.getGreen(), corNucleo.getBlue(), 40) });
        g2d.setPaint(nucleoGrad);
        g2d.fill(new Ellipse2D.Double(-raioNucleo, -130 - raioNucleo, raioNucleo * 2, raioNucleo * 2));
        g2d.setColor(new Color(30, 60, 90));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new Ellipse2D.Double(-13, -143, 26, 26));

        // ---------- Braços / mãos ----------
        g2d.setColor(new Color(60, 60, 70));
        g2d.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(-50, -160, -90, -130));
        g2d.draw(new Line2D.Double(50, -160, 90, -130));
        g2d.setColor(Color.ORANGE);
        g2d.fill(new Ellipse2D.Double(-108, -138, 16, 16)); // mão esquerda
        g2d.fill(new Ellipse2D.Double(92, -138, 16, 16));   // mão direita

        // ---------- Cabeça (degradê + viseira) ----------
        GradientPaint cabecaGrad = new GradientPaint(0, -260, Color.WHITE, 0, -180, new Color(150, 155, 165));
        g2d.setPaint(cabecaGrad);
        g2d.fill(new RoundRectangle2D.Double(-50, -260, 100, 80, 18, 18));

        g2d.setColor(new Color(20, 25, 35));
        g2d.fill(new RoundRectangle2D.Double(-38, -235, 76, 32, 14, 14)); // viseira escura

        // ---------- Olhos (brilho ciano com radial gradient) ----------
        Color corOlho = new Color(60, 230, 255);
        RadialGradientPaint olhoGrad = new RadialGradientPaint(
                new Point2D.Double(-15, -220), 11f,
                new float[] { 0f, 1f }, new Color[] { Color.WHITE, corOlho });
        g2d.setPaint(olhoGrad);
        g2d.fill(new Ellipse2D.Double(-24, -228, 18, 16));
        RadialGradientPaint olhoGrad2 = new RadialGradientPaint(
                new Point2D.Double(15, -220), 11f,
                new float[] { 0f, 1f }, new Color[] { Color.WHITE, corOlho });
        g2d.setPaint(olhoGrad2);
        g2d.fill(new Ellipse2D.Double(6, -228, 18, 16));

        // ---------- Antena com luz pulsante ----------
        g2d.setColor(new Color(90, 90, 100));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.draw(new Line2D.Double(0, -260, 0, -340));
        Color corAntena = Color.getHSBColor((float) ((t * 0.5) % 1.0), 0.8f, 1f);
        g2d.setColor(corAntena);
        g2d.fill(new Ellipse2D.Double(-6, -348, 12, 12));

        g2d.setColor(new Color(30, 30, 40));
        g2d.setStroke(new BasicStroke(2.2f));
        g2d.draw(contorno);
    }

    /**
     * GeneralPath único - Requisito 1.
     */
    private GeneralPath construirContorno() {
        GeneralPath gp = new GeneralPath();

        gp.append(new RoundRectangle2D.Double(-50, -260, 100, 80, 18, 18), false); // cabeça
        gp.append(new QuadCurve2D.Double(-25, -215, -15, -228, -5, -215), false);  // olho esquerdo
        gp.append(new QuadCurve2D.Double(5, -215, 15, -228, 25, -215), false);     // olho direito
        gp.append(new CubicCurve2D.Double(-18, -204, -8, -196, 8, -196, 18, -204), false); // boca (Bézier cúbica)
        gp.append(new Line2D.Double(0, -260, 0, -340), false);                     // haste da antena
        gp.append(new Ellipse2D.Double(-6, -348, 12, 12), false);                  // luz da antena
        gp.append(new RoundRectangle2D.Double(-50, -180, 100, 120, 10, 10), false); // corpo
        gp.append(new RoundRectangle2D.Double(-68, -172, 26, 32, 8, 8), false);    // ombreira esquerda
        gp.append(new RoundRectangle2D.Double(42, -172, 26, 32, 8, 8), false);     // ombreira direita
        gp.append(new Ellipse2D.Double(-108, -138, 16, 16), false);                // mão esquerda
        gp.append(new Ellipse2D.Double(92, -138, 16, 16), false);                  // mão direita
        gp.append(new Rectangle2D.Double(-35, -60, 20, 60), false);                // perna esquerda
        gp.append(new Rectangle2D.Double(15, -60, 20, 60), false);                 // perna direita
        gp.append(new RoundRectangle2D.Double(-40, -10, 28, 10, 4, 4), false);     // bota esquerda
        gp.append(new RoundRectangle2D.Double(12, -10, 28, 10, 4, 4), false);      // bota direita

        return gp;
    }

    private void desenharCanhao(Graphics2D g2d) {
        AffineTransform transformOriginal = g2d.getTransform();

        g2d.translate(getPivoCanhaoX(), getPivoCanhaoY());
        g2d.rotate(anguloCanhao); // rotação em direção ao ponteiro do mouse

        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(new Ellipse2D.Double(-7, -7, 14, 14)); // base articulada
        g2d.setColor(new Color(200, 60, 60));
        g2d.fill(new Rectangle2D.Double(0, -5, COMPRIMENTO_CANO, 10)); // cano
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(new Rectangle2D.Double(0, -5, COMPRIMENTO_CANO, 10));

        g2d.setTransform(transformOriginal);
    }
}
