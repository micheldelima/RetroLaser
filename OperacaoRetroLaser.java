import javax.swing.*;

/**
 * OPERAÇÃO RETROLASER - DEFENDER 2D
 * Aluno: Michel de Lima RA: 21013723
 * Disciplina: Computação Gráfica - Engenharia de Software - UEPG
 * Tecnologia: Java 2D
 * Classe principal: cria a janela do jogo (JFrame) e inicializa o game loop.
 */
public class OperacaoRetroLaser extends JFrame {

    public static final int LARGURA = 960;
    public static final int ALTURA = 680;

    public OperacaoRetroLaser() {
        super("Operação RetroLaser - Defender 2D | UEPG | Michel de Lima - RA 21013723");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel painel = new GamePanel(LARGURA, ALTURA);
        add(painel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        painel.requestFocusInWindow();
        painel.iniciarLoop();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OperacaoRetroLaser::new);
    }
}
