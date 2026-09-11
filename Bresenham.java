import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Requisito 2 - Sistema de Laser.
 *
 * Implementação própria (manual) do Algoritmo de Bresenham .
 */
public class Bresenham {

    public static List<Point> linha(int x0, int y0, int x1, int y1) {
        List<Point> pontos = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int erro = dx + dy;

        int x = x0, y = y0;
        while (true) {
            pontos.add(new Point(x, y));
            if (x == x1 && y == y1) break;
            int e2 = 2 * erro;
            if (e2 >= dy) { erro += dy; x += sx; }
            if (e2 <= dx) { erro += dx; y += sy; }
        }
        return pontos;
    }
}
