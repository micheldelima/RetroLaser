import java.awt.*;
import java.awt.geom.*;

/**
 * Requisito 4 - Alvos e Mecânica Básica de Jogo.
 * Drone/asteroide que surge no topo da tela e desce em direção ao solo.
 */
public class Enemy {

    private final double x;
    private double y;
    private final int tamanho;
    private double anguloRotor = 0;
    private boolean vivo = true;

    public Enemy(double x, double y, int tamanho) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
    }

    public void atualizar(double velocidade) {
        y += velocidade;
        anguloRotor += 0.3;
    }

    public boolean isVivo() { return vivo; }
    public void destruir() { vivo = false; }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getTamanho() { return tamanho; }

    public boolean saiuDaTela(int altura) { return y > altura; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, tamanho, tamanho);
    }

    public void desenhar(Graphics2D g2d) {
        AffineTransform transformOriginal = g2d.getTransform();
        double cx = x + tamanho / 2.0;
        double cy = y + tamanho / 2.0;
        g2d.translate(cx, cy);

        g2d.setColor(new Color(180, 180, 190));
        g2d.fill(new Ellipse2D.Double(-tamanho / 2.0, -tamanho / 2.0, tamanho, tamanho));
        g2d.setColor(Color.BLACK);
        g2d.draw(new Ellipse2D.Double(-tamanho / 2.0, -tamanho / 2.0, tamanho, tamanho));

        g2d.rotate(anguloRotor);
        g2d.setColor(new Color(90, 90, 100));
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new Line2D.Double(-tamanho * 0.7, 0, tamanho * 0.7, 0));
        g2d.draw(new Line2D.Double(0, -tamanho * 0.7, 0, tamanho * 0.7));

        g2d.setTransform(transformOriginal);
    }
}
