/*
Autor: Filip Przygoński
*/

import java.awt.*;
import java.io.Serializable;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Klasa która reprezentuje połączenie między dwoma wierzchołkami grafu, a zarazem połączenie między dwoma przystankami.
 */
public class Edge implements IMoving, Serializable {

    private Vertex firstVertex;
    private Vertex secondVertex;
    private int width;
    private Color color;

    public Edge(Vertex firstVertex, Vertex secondVertex, Color color) {
        this.firstVertex = firstVertex;
        this.secondVertex = secondVertex;
        this.width = 2;
        this.color = color;
    }

    /**
     * Rysuje krawędź.
     *
     * @param g obiekt klasy Graphics odpowiedzialny za rysowanie
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine(firstVertex.getX(), firstVertex.getY(), secondVertex.getX(), secondVertex.getY());
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Przesuwa krawędź.
     *
     * @param dx o ile pikseli w prawo
     * @param dy o ile pikseli w górę
     */
    @Override
    public void move(int dx, int dy) {
        firstVertex.move(dx, dy);
        secondVertex.move(dx, dy);
    }

    /**
     * Sprawdza, czy dane współrzędne są na krawędzi (wystarczająco blisko krawędzi, bo margines błędu).
     * @param px współrzędna x punktu
     * @param py współrzędna y punktu
     * @return czy punkt jest na tej krawędzi
     */
    public boolean isPointOnEdge(int px, int py) {
        int x1 = firstVertex.getX();
        int y1 = firstVertex.getY();
        int x2 = secondVertex.getX();
        int y2 = secondVertex.getY();
        if (!((x1 < px && px < x2) || (x1 > px && px > x2))) {
            return false;
        } else if (!((y1 < py && py < y2) || (y1 > py && py > y2))) {
            return false;
        }
        int a = y1 - y2;
        int b = x2 - x1;
        int c = x1 * y2 - x2 * y1;
        double distance;
        try {
            distance = abs(a * px + b * py + c) / sqrt(a * a + b * b);
        } catch (ArithmeticException e) {
            return false;
        }
        return distance <= width + 1; //margines błędu
    }

    @Override
    public String toString() {
        return firstVertex.toString() + " -> " + secondVertex.toString();
    }

    public Vertex getFirstVertex() {
        return firstVertex;
    }

    public Vertex getSecondVertex() {
        return secondVertex;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
