import java.awt.*;

/**
 * Extra: Animação & Glow Neon - "Pontuação Flutuante".
 */
public class FloatingText {

    private final String texto;
    private double x, y;
    private float alpha = 1.0f;

    public FloatingText(String texto, double x, double y) {
        this.texto = texto;
        this.x = x;
        this.y = y;
    }

    public void atualizar() {
        y -= 0.8;
        alpha -= 0.02f;
        if (alpha < 0) alpha = 0;
    }

    public boolean terminou() { return alpha <= 0; }

    public void desenhar(Graphics2D g2d, Font fonte) {
        Composite original = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setFont(fonte);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(texto, (float) x, (float) y);
        g2d.setComposite(original);
    }
}
