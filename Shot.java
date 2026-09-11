import java.awt.*;
import java.util.List;

/**
 * Representa um disparo de laser: a sequência de pixels calculada pelo
 * Algoritmo de Bresenham. O núcleo do laser é desenhado
 * pixel a pixel com fillRect (nunca com g2d.drawLine()), e um efeito
 * "neon" extra (linhas paralelas com gradiente de cor/transparência)
 * é somado por cima usando AlphaComposite.
 */
public class Shot {

    private final List<Point> pontos;
    private final double dirX, dirY;
    private int vida = 8;

    public Shot(List<Point> pontos, double dirX, double dirY) {
        this.pontos = pontos;
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public void atualizar() { vida--; }

    public boolean expirou() { return vida <= 0; }

    /** Requisito 4: interseção entre os pixels do laser (Bresenham) e o alvo. */
    public boolean atingiu(Rectangle alvo) {
        for (Point p : pontos) {
            if (alvo.contains(p)) return true;
        }
        return false;
    }

    public void desenhar(Graphics2D g2d) {
        float alpha = Math.max(0f, vida / 8.0f);
        Composite composOriginal = g2d.getComposite();

        // ---------- Extra: efeito neon (linhas paralelas via Bresenham) ----------
        double perpX = -dirY, perpY = dirX;
        int[] deslocamentos = { -3, -2, -1, 1, 2, 3 };
        for (int d : deslocamentos) {
            float a = alpha * (0.35f - Math.abs(d) * 0.06f);
            if (a <= 0) continue;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
            g2d.setColor(new Color(255, 90, 150));
            for (Point p : pontos) {
                int px = (int) Math.round(p.x + perpX * d);
                int py = (int) Math.round(p.y + perpY * d);
                g2d.fillRect(px, py, 2, 2);
            }
        }

        // ---------- Requisito 2: núcleo do laser, rasterizado pixel a pixel ----------
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Color.RED);
        for (Point p : pontos) {
            g2d.fillRect(p.x, p.y, 2, 2);
        }

        g2d.setComposite(composOriginal);
    }
}
