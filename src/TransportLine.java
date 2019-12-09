/*
Autor: Filip Przygoński
*/

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Klasa reprezentująca linię komunikacji miejskiej.
 */
public class TransportLine implements Serializable {

    public static final byte BUS = 1;
    public static final byte TRAM = 2;

    private String lineNumber; // String bo np. 0L, 0P
    /**
     * Ostatni przystanek linii
     */
    private String destination;
    private byte transportType;
    private ArrayList<Vertex> stations;

    public TransportLine(String lineNumber, byte transportType) {
        this.lineNumber = lineNumber;
        this.destination = "";
        this.transportType = transportType;
        this.stations = new ArrayList<>();
    }

    /**
     * Dodaje na sam koniec linii następny przystanek
     * @param station przystanek do dodania
     */
    public void addStation(Vertex station) {
        stations.add(station);
        destination = station.toString();
    }

    /**
     * Sprawdza, czy dany przystanek jest już w linii
     * @param station przystanek
     * @return czy przystanek jest już w linii
     */
    public boolean containsStation(Vertex station) {
        for (Vertex vertex : stations) {
            if (station == vertex) return true;
        }
        return false;
    }

    /**
     * Sprawdza, czy linia jedzie danym połączeniem
     * @param connection połączenie
     * @return czy linia jedzie danym połączeniem
     */
    public boolean containsConnection(Edge connection) {
        Vertex firstVertex = connection.getFirstVertex();
        Vertex secondVertex = connection.getSecondVertex();
        int size = stations.size();
        for (int i = 0; i < size - 1; ++i) {
            Vertex station1 = stations.get(i);
            Vertex station2 = stations.get(i + 1);
            boolean isConnectionInLine = (firstVertex == station1 && secondVertex == station2) || (firstVertex == station2 && secondVertex == station1);
            if (isConnectionInLine)
                return true;
        }
        return false;
    }

    /**
     * Zwraca listę wszystkich stacji po przecinku
     * @return lista wszystkich stacji po przecinku
     */
    public String listOfStations() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Vertex vertex : stations) {
            stringBuilder.append(vertex.toString()).append(", ");
        }
        int stringBuilderLength = stringBuilder.length();
        stringBuilder.delete(stringBuilderLength - 2, stringBuilderLength - 1);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        String type;
        if (transportType == BUS)
            type = "Autobus";
        else if (transportType == TRAM)
            type = "Tramwaj";
        else type = "Nieznany";
        return lineNumber + " " + destination + " " + type;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDestination() {
        return destination;
    }

    public byte getTransportType() {
        return transportType;
    }

    public void setTransportType(byte transportType) {
        this.transportType = transportType;
    }

    public ArrayList<Vertex> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Vertex> stations) {
        this.stations = stations;
    }
}
