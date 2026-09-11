import java.awt.*;

/** Estrela de fundo (efeito parallax simples da estação Aegis-7 no espaço). */
public class Star {

    private final int x, tamanho;
    private int y;

    public Star(int x, int y, int tamanho) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
    }

    public void atualizar(int altura) {
        y += tamanho;
        if (y > altura) y = 0;
    }

    public void desenhar(Graphics2D g2d) {
        g2d.fillRect(x, y, tamanho, tamanho);
    }
}
