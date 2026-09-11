import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

/**
 * Requisito 3 - HUD e Interface Tipográfica.
 *
 * Utilitários de manipulação de caracteres em Java 2D:
 *  - FontMetrics para alinhamento dinâmico / centralização de strings;
 *  - GlyphVector.getOutline() para extrair o contorno do texto;
 *  - GradientPaint para preencher o contorno com um degradê de cor;
 *  - sombra projetada (string duplicada com offset);
 *  - transformação afim de cisalhamento (texto inclinado / itálico customizado).
 */
public class TextFX {

    public static void desenharComSombra(Graphics2D g2d, String texto, int x, int y, Color cor) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(texto, x + 2, y + 2); // sombra projetada (offset)
        g2d.setColor(cor);
        g2d.drawString(texto, x, y);
    }

    /** Requisito 3: usa FontMetrics para calcular a largura da string e centralizá-la. */
    public static int centralizarX(Graphics2D g2d, String texto, int larguraTela) {
        FontMetrics fm = g2d.getFontMetrics();
        return (larguraTela - fm.stringWidth(texto)) / 2;
    }

    /**
     * Desenha um texto centralizado com contorno extraído via
     * GlyphVector.getOutline() e preenchido com um degradê (GradientPaint).
     * Usado nas telas de Título, Pause e Game Over.
     */
    public static void desenharCentralizadoComGradiente(Graphics2D g2d, String texto, Font fonte,
            int larguraTela, int y, Color corTopo, Color corBase) {

        Font fonteAntiga = g2d.getFont();
        g2d.setFont(fonte);
        int x = centralizarX(g2d, texto, larguraTela);

        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = fonte.createGlyphVector(frc, texto);
        Shape contorno = gv.getOutline(x, y); // Requisito 3: contorno via GlyphVector

        // sombra projetada do título
        AffineTransform deslocSombra = AffineTransform.getTranslateInstance(4, 4);
        g2d.setColor(new Color(0, 0, 0, 130));
        g2d.fill(deslocSombra.createTransformedShape(contorno));

        // preenchimento com gradiente
        FontMetrics fm = g2d.getFontMetrics();
        GradientPaint gradiente = new GradientPaint(x, y - fm.getAscent(), corTopo, x, y, corBase);
        g2d.setPaint(gradiente);
        g2d.fill(contorno);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(contorno);

        g2d.setFont(fonteAntiga);
    }

    /** Extra: texto deformado com transformação afim de cisalhamento (itálico customizado). */
    public static void desenharInclinado(Graphics2D g2d, String texto, Font fonte,
            int larguraTela, int y, Color cor) {
        Font fonteAntiga = g2d.getFont();

        AffineTransform af = new AffineTransform();
        af.shear(-0.25, 0);
        Font fonteInclinada = fonte.deriveFont(af);

        g2d.setFont(fonteInclinada);
        int x = centralizarX(g2d, texto, larguraTela);
        g2d.setColor(cor);
        g2d.drawString(texto, x, y);

        g2d.setFont(fonteAntiga);
    }
}
