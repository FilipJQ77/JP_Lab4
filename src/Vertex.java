/*
    Program: Aplikacja z możliwością działań na sieci komunikacji miejskiej
    Plik: Vertex.java
    Autor: Filip Przygoński, 248892
    Data: Grudzień 2019
*/

import java.awt.*;
import java.io.Serializable;

/**
 * TODO DOKUMENTACJA
 */
public class Vertex implements IMoving, Serializable {

    private int x;
    private int y;
    private int r;
    private String name;
    private Color color;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
        this.r = 5;
        this.name = "";
        this.color = Color.WHITE;
    }

    public Vertex(int x, int y, String name, Color color) {
        this.x = x;
        this.y = y;
        this.r = 10;
        this.name = name;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        g.setColor(Color.BLACK);
        g.drawString(name, x, y - r);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
    }

    /**
     * przesuwa wierzchołek
     *
     * @param dx o ile pikseli w prawo
     * @param dy o ile pikseli w górę
     */
    @Override
    public void move(int dx, int dy) {
        x += dx;
        y -= dy;
    }

    public boolean isPointInVertex(int px, int py) {
        return (px - x) * (px - x) + (py - y) * (py - y) <= r * r;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
