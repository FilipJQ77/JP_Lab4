/*
Autor: Filip Przygoński
*/

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Klasa reprezentująca graf linii transportu miejskiego
 */
public class Graph implements Serializable, IMoving {

    private static final long serialVersionUID = 5922347028700915406L;

    /**
     * wszystkie stacje
     */
    private ArrayList<Vertex> stations;

    /**
     * wszystkie linie transportu
     */
    private ArrayList<TransportLine> transportLines;

    /**
     * wszystkie połączenia między dwoma przystankami
     */
    private ArrayList<Edge> connections;

    public Graph() {
        this.stations = new ArrayList<>();
        this.transportLines = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    /**
     * dodaje przystanek, jednocześnie sortując listę wszystkich przystanków alfabrtycznie
     *
     * @param station
     */
    public void addStation(Vertex station) {
        stations.add(station);
        Comparator<Vertex> comparator = (station1, station2) -> {
            String name1 = station1.getName();
            String name2 = station2.getName();
            return name1.compareTo(name2);
        };
        stations.sort(comparator);
    }

    /**
     * usuwa stację, tylko gdy można ją usunąć (żadna linia nie przejeżdża przez stację)
     *
     * @param station stacja do usunięcia
     * @return czy można usunąć stację (czy stacja została usunięta)
     */
    public boolean removeStation(Vertex station) {
        for (Edge connection : connections) {
            Vertex firstVertex = connection.getFirstVertex();
            Vertex secondVertex = connection.getSecondVertex();
            if (station == firstVertex)
                return false;
            else if (station == secondVertex)
                return false;
        }
        stations.remove(station);
        return true;
    }

    /**
     * dodaje nową linię transportu, jednocześnie sortuje listę linii transportu według numeru linii, oraz zaznacza linię na grafie odpowiednim kolorem
     *
     * @param transportLine nowa linia transportu
     */
    public void addTransportLine(TransportLine transportLine) {
        transportLines.add(transportLine);
        Comparator<TransportLine> comparator = (transportLine1, transportLine2) -> {
            String lineNumber1 = transportLine1.getLineNumber();
            String lineNumber2 = transportLine2.getLineNumber();
            int check = lineNumber1.compareTo(lineNumber2);
            try {
                int lineNumber1ToNumber = Integer.parseInt(lineNumber1);
                int lineNumber2ToNumber = Integer.parseInt(lineNumber2);
                check = Integer.compare(lineNumber1ToNumber, lineNumber2ToNumber);
            } catch (Exception ignored) {
            }
            if (check != 0) return check;
            else {
                String destination1 = transportLine1.getDestination();
                String destination2 = transportLine2.getDestination();
                return destination1.compareTo(destination2);
            }
        };
        transportLines.sort(comparator);
        Color color;
        byte transportType = transportLine.getTransportType();
        if (transportType == TransportLine.BUS) {
            color = Color.ORANGE;
        } else {
            color = new Color(0, 153, 255);
        }
        ArrayList<Vertex> stations = transportLine.getStations();
        int size = stations.size();
        for (int i = 0; i < size - 1; ++i) {
            Vertex station1 = stations.get(i);
            Vertex station2 = stations.get(i + 1);
            boolean doAddConnection = true;
            for (Edge connection : connections) {
                Vertex firstVertex = connection.getFirstVertex();
                Vertex secondVertex = connection.getSecondVertex();
                boolean isAddedConnectionAlreadyInGraph = (firstVertex == station1 && secondVertex == station2) || (firstVertex == station2 && secondVertex == station1);
                if (isAddedConnectionAlreadyInGraph) {
                    doAddConnection = false;
                    connection.setWidth(connection.getWidth() + 2);
                    if (!connection.getColor().equals(color)) {
                        connection.setColor(Color.GREEN);
                    }
                    break;
                }
            }
            if (doAddConnection) {
                addConnection(station1, station2, color);
            }
        }
    }

    /**
     * usuwa daną linię transportu
     *
     * @param transportLine linia transportu do usunięcia
     */
    public void removeTransportLine(TransportLine transportLine) {
        //TODO pomyśleć czy dałoby się optymalniej
        ArrayList<TransportLine> newTransportLines = new ArrayList<>();
        for (TransportLine line : transportLines) {
            if (line != transportLine) {
                newTransportLines.add(line);
            }
        }
        transportLines.clear();
        connections.clear();
        for (TransportLine line : newTransportLines) {
            addTransportLine(line);
        }
    }

    /**
     * dodaje nowe połączenie między przystankami
     *
     * @param station1 pierwszy przystanek
     * @param station2 drugi przystanek
     * @param color    kolor połączenia, zależy od rodzaju transportu
     */
    private void addConnection(Vertex station1, Vertex station2, Color color) {
        Edge newEdge = new Edge(station1, station2, color);
        connections.add(newEdge);
    }

    /**
     * rysuje cały graf
     *
     * @param g obiekt klasy Graphics odpowiedzialny za rysowanie
     */
    public void draw(Graphics g) {
        for (Edge connection : connections) {
            connection.draw(g);
        }
        for (Vertex station : stations) {
            station.draw(g);
        }
    }

    /**
     * przesuwa graf
     *
     * @param dx o ile pikseli w prawo
     * @param dy o ile pikseli w górę
     */
    @Override
    public void move(int dx, int dy) {
        for (Vertex vertex : stations) {
            vertex.move(dx, dy);
        }
    }

    /**
     * metoda sprawdzająca, czy dane współrzędne punktu znajdują się na którymkolwiek wierzchołku
     *
     * @param mx współrzędna x punktu
     * @param my współrzędna y punktu
     * @return wierzchołek do którego należy dany punkt (lub null, jeśli punkt nie należy do żadnego wierzchołka)
     */
    public Vertex returnVertexContainingAPoint(int mx, int my) {
        for (Vertex vertex : stations) {
            if (vertex.isPointInVertex(mx, my)) return vertex;
        }
        return null;
    }

    /**
     * metoda sprawdzająca, czy dane współrzędne punktu znajdują się na którejkolwiek krawędzi
     * @param mx współrzędna x punktu
     * @param my współrzędna y punktu
     * @return krawędź do której należy dany punkt (lub null, jeśli punkt nie należy do żadnej krawędzi)
     */
    public Edge returnEdgeContainingAPoint(int mx, int my) {
        for (Edge edge : connections) {
            if (edge.isPointOnEdge(mx, my)) return edge;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Vertices:");
        for (Vertex vertex : stations) {
            stringBuilder.append(" ").append(vertex.toString()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("\nEdges:");
        for (Edge edge : connections) {
            stringBuilder.append(" ").append(edge.toString()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public ArrayList<Vertex> getStations() {
        return stations;
    }

    public ArrayList<TransportLine> getTransportLines() {
        return transportLines;
    }

    public ArrayList<Edge> getConnections() {
        return connections;
    }
}
