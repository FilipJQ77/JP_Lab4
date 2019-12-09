/*
Autor: Filip Przygoński
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Pomocnicza tabela wyświetlająca różne informacje
 */
class TableFrame extends JFrame {

    DefaultTableModel tableModel;
    JTable table;
    JScrollPane scrollPane;

    /**
     *
     * @param title tytuł okienka
     * @param tableHeader opis kolumn
     * @param arrayLists informacje które mają zostać wyświetlone
     */
    TableFrame(String title, String[] tableHeader, ArrayList<ArrayList<String>> arrayLists) {
        super(title);
        setSize(1000, 600);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        tableModel = new DefaultTableModel(tableHeader, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //aby tabela była nieedytowalna
            }
        };

        int howManyRows = arrayLists.get(0).size();//wszystkie listy mają taką samą długość
        for (int i = 0; i < howManyRows; ++i) {
            ArrayList<String> row = new ArrayList<>();
            for (ArrayList<String> arrayList : arrayLists) {
                String string = arrayList.get(i);
                row.add(string);
            }
            tableModel.addRow(row.toArray());
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(this.getWidth() - 10, this.getHeight() - 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        add(scrollPane);
        setVisible(true);
    }
}

/**
 * Klasa odpowiadająca za wyświetlanie grafu
 */
public class GraphPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Graph graph;

    /**
     * obecnie zaznaczony przystanek
     */
    private Vertex currentVertex;

    /**
     * obecnie zaznaczone połączenie
     */
    private Edge currentEdge;

    private int mouseX;
    private int mouseY;

    /**
     * boolean pamiętający, czy jest trzymany lewy przycisk myszki podczas przesuwania myszki
     */
    private boolean dragging;

    /**
     * boolean pamiętający, czy jakaś linia transportu jest zaznaczona
     */
    private boolean lineHighlighted;

    public GraphPanel() {
        super();
        graph = new Graph();
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        setSize(1280, 720);
        setBackground(new Color(150, 150, 150));
        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    /**
     * zaznacza przystanek (powiększa go)
     */
    private void highlightVertex() {
        if (currentVertex != null) {
            currentVertex.setR(currentVertex.getR() + 5);
        }
    }

    /**
     * odznacza przystanek (pomniejsza go)
     */
    private void unhighlightVertex() {
        if (currentVertex != null) {
            currentVertex.setR(currentVertex.getR() - 5);
        }
    }

    /**
     * zaznacza połączenie (powiększa je)
     */
    private void highlightEdge() {
        if (currentEdge != null) {
            currentEdge.setWidth(currentEdge.getWidth() + 3);
        }
    }

    /**
     * odznacza połączenie (pomniejsza je)
     */
    private void unhighlightEdge() {
        if (currentEdge != null) {
            currentEdge.setWidth(currentEdge.getWidth() - 3);
        }
    }

    /**
     * metoda zajmująca się usuwaniem stacji
     */
    private void removeStation() {
        if (!graph.removeStation(currentVertex)) {
            JOptionPane.showMessageDialog(this, "Nie można usunąć przystanku przez który przejeżdżają jakiekolwiek linie!", "Błąd", JOptionPane.ERROR_MESSAGE);
        } else {
            currentVertex = null;
        }
    }

    /**
     * jeśli kliknięto LPM na jakiś element, to jest on zaznaczony i zapamiętany
     *
     * @param x współrzędna x punktu kliknięcia
     * @param y współrzędna y punktu kliknięcia
     */
    void leftClick(int x, int y) {
        unhighlightVertex();
        unhighlightEdge();
        currentVertex = null;
        currentEdge = null;
        currentVertex = graph.returnVertexContainingAPoint(x, y);
        if (currentVertex == null) {
            currentEdge = graph.returnEdgeContainingAPoint(x, y);
        }
        highlightVertex();
        highlightEdge();
    }

    /**
     * jeśli kliknięto PPM na jakiś element, to jest on zaznaczony, zapamiętany, i wyskakuje popup menu pozwalające na edycję/wyświetlenie dodatkowych informacji o elemencie
     *
     * @param x współrzędna x punktu kliknięcia
     * @param y współrzędna y punktu kliknięcia
     */
    void rightClick(int x, int y) {
        leftClick(x, y);
        createPopupMenu(x, y);
    }

    /**
     * zajmuje się tworzeniem popup menu
     * @param x współrzędna x punktu kliknięcia
     * @param y współrzędna y punktu kliknięcia
     */
    void createPopupMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        if (currentVertex != null) {
            JMenuItem menuChangeName = new JMenuItem("Zmień nazwę");
            menuChangeName.addActionListener(actionEvent -> {
                String newName = JOptionPane.showInputDialog(this, "Podaj nową nazwę przystanku", "Nazwa przystanku", JOptionPane.PLAIN_MESSAGE);
                if (newName == null) return;
                currentVertex.setName(newName);
                repaint();
            });
            JMenuItem menuChangeColor = new JMenuItem("Zmień kolor");
            menuChangeColor.addActionListener(actionEvent -> {
                Color newColor = JColorChooser.showDialog(this, "Wybierz nowy kolor przystanku", null);
                if (newColor != null) {
                    currentVertex.setColor(newColor);
                }
                repaint();
            });
            JMenuItem menuChangeR = new JMenuItem("Zmień wielkość");
            menuChangeR.addActionListener(actionEvent -> {
                int newR;
                try {
                    newR = Integer.parseInt(JOptionPane.showInputDialog(this, "Podaj nowy promień (w pikselach)", "Wielkość", JOptionPane.PLAIN_MESSAGE));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newR > 0) {
                    currentVertex.setR(newR + 5);//+5, bo jest zaznaczony, jak przestanie być zaznaczony, będzie danego rozmiaru
                }
                repaint();
            });
            JMenuItem menuShowTransportLines = new JMenuItem("Pokaż linie jadące przez ten przystanek");
            menuShowTransportLines.addActionListener(actionEvent -> {
                ArrayList<TransportLine> transportLines = graph.getTransportLines();
                ArrayList<String> lineNumbers = new ArrayList<>();
                ArrayList<String> lineDestinations = new ArrayList<>();
                for (TransportLine transportLine : transportLines) {
                    if (transportLine.containsStation(currentVertex)) {
                        lineNumbers.add(transportLine.getLineNumber());
                        lineDestinations.add(transportLine.getDestination());
                    }
                }
                String[] tableHeader = {"Numer linii", "Kierunek"};
                ArrayList<ArrayList<String>> data = new ArrayList<>();
                data.add(lineNumbers);
                data.add(lineDestinations);
                new TableFrame("Linie jadące przez " + currentVertex.getName(), tableHeader, data);
            });
            JMenuItem menuDelete = new JMenuItem("Usuń przystanek");
            menuDelete.addActionListener(actionEvent -> {
                removeStation();
                repaint();
            });
            menu.add(menuChangeName);
            menu.add(menuChangeColor);
            menu.add(menuChangeR);
            menu.add(menuShowTransportLines);
            menu.add(menuDelete);
        } else if (currentEdge != null) {
            JMenuItem menuShowTransportLines = new JMenuItem("Pokaż linie jadące tą trasą");
            menuShowTransportLines.addActionListener(actionEvent -> {
                ArrayList<TransportLine> transportLines = graph.getTransportLines();
                ArrayList<String> lineNumbers = new ArrayList<>();
                ArrayList<String> lineDestinations = new ArrayList<>();
                for (TransportLine transportLine : transportLines) {
                    if (transportLine.containsConnection(currentEdge)) {
                        lineNumbers.add(transportLine.getLineNumber());
                        lineDestinations.add(transportLine.getDestination());
                    }
                }
                String[] tableHeader = {"Numer linii", "Kierunek"};
                ArrayList<ArrayList<String>> data = new ArrayList<>();
                data.add(lineNumbers);
                data.add(lineDestinations);
                new TableFrame("Linie jadące między " + currentEdge.getFirstVertex().toString() + ", a " + currentEdge.getSecondVertex().toString(), tableHeader, data);
            });
            menu.add(menuShowTransportLines);
        } else {
            JMenuItem menuCreateNewStation = new JMenuItem("Stwórz nowy przystanek");
            menuCreateNewStation.addActionListener(actionEvent -> {
                String name = JOptionPane.showInputDialog(this, "Podaj nazwę nowego przystanku", "Nowy przystanek", JOptionPane.PLAIN_MESSAGE);
                if (name == null) return;
                Color newColor = JColorChooser.showDialog(this, "Wybierz nowy kolor przystanku", null);
                if (newColor == null) return;
                Vertex newVertex = new Vertex(x, y, name, newColor);
                graph.addStation(newVertex);
                repaint();
            });
            menu.add(menuCreateNewStation);
        }
        menu.show(this, x, y);
        unhighlightTransportLine();
    }

    /**
     *
     * @return który obiekt powinien zostać poruszony
     */
    IMoving objectToMove() {
        if (currentVertex != null) return currentVertex;
        else if (currentEdge != null) return currentEdge;
        else return graph;
    }

    /**
     * tworzy nowe okno i wyświetla w nim wszystkie stacje oraz liczbę linii zatrzymujących się tam
     */
    void showStations() {
        ArrayList<String> stationsList = new ArrayList<>();
        ArrayList<String> stationsAmountOfLineslist = new ArrayList<>();
        ArrayList<Vertex> stations = graph.getStations();
        ArrayList<TransportLine> transportLines = graph.getTransportLines();
        for (Vertex station : stations) {
            String stationName = station.getName();
            stationsList.add(stationName);
            int howManyLines = 0;
            for (TransportLine transportLine : transportLines) {
                if (transportLine.containsStation(station)) ++howManyLines;
            }
            stationsAmountOfLineslist.add(String.valueOf(howManyLines));
        }
        String[] tableHeader = {"Nazwa przystanku", "Ilość linii zatrzymujących się na tym przystanku"};
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(stationsList);
        data.add(stationsAmountOfLineslist);
        new TableFrame("Wszystkie przystanki", tableHeader, data);
    }

    /**
     * tworzy nowe okno i wyświetla w nim wszystkie przystanki wybranej linii
     */
    void showTransportLineStations() {
        ArrayList<TransportLine> transportLines = graph.getTransportLines();
        TransportLine transportLine = (TransportLine) JOptionPane.showInputDialog(this, "Wybierz linię", "", JOptionPane.PLAIN_MESSAGE, null, transportLines.toArray(), null);
        ArrayList<String> stationNames = new ArrayList<>();
        ArrayList<String> stationNumbers = new ArrayList<>();
        ArrayList<Vertex> stations = transportLine.getStations();
        int counter = 1;
        for (Vertex station : stations) {
            String stationName = station.getName();
            stationNames.add(stationName);
            stationNumbers.add(String.valueOf(counter));
            ++counter;
        }
        String[] tableHeader = {"Nazwa przystanku", "Numer"};
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(stationNames);
        data.add(stationNumbers);
        new TableFrame("Przystanki linii " + transportLine.getLineNumber() + " " + transportLine.getDestination(), tableHeader, data);
    }

    /**
     * zajmuje się tworzeniem przez użytkownika nowej linii transportu
     */
    void createTransportLine() {
        String lineNumber = JOptionPane.showInputDialog(this, "Podaj numer linii", "", JOptionPane.PLAIN_MESSAGE);
        if (lineNumber == null)
            return;
        //TODO wybór trasportu można lepiej
        String bus = "Autobus";
        String tram = "Tramwaj";
        String[] transportTypes = {bus, tram};
        String selectedType = (String) JOptionPane.showInputDialog(this, "Wybierz typ transportu", "", JOptionPane.PLAIN_MESSAGE, null, transportTypes, null);
        if (selectedType == null)
            return;
        byte transportType;
        if (selectedType.equals(bus)) {
            transportType = TransportLine.BUS;
        } else transportType = TransportLine.TRAM;
        TransportLine transportLine = new TransportLine(lineNumber, transportType);
        editTransportLineStations(transportLine);
        if (transportLine.getStations().size() == 0) return;
        graph.addTransportLine(transportLine);
        repaint();
    }

    /**
     * zajmuje się edytowaniem przez użytkownika danej linii transportu
     */
    void editTransportLine() {
        TransportLine transportLine = (TransportLine) JOptionPane.showInputDialog(this, "Wybierz którą linię chcesz edytować", "", JOptionPane.PLAIN_MESSAGE, null, graph.getTransportLines().toArray(), null);
        if (transportLine != null) {
            String lineNumber = JOptionPane.showInputDialog("Podaj numer linii", transportLine.getLineNumber());
            if (lineNumber == null)
                return;
            //TODO wybór trasportu można lepiej
            String bus = "Autobus";
            String tram = "Tramwaj";
            String[] transportTypes = {bus, tram};
            String selectedType = (String) JOptionPane.showInputDialog(this, "Wybierz typ transportu", "", JOptionPane.PLAIN_MESSAGE, null, transportTypes, null);
            if (selectedType == null)
                return;
            byte transportType;
            if (selectedType.equals(bus)) {
                transportType = TransportLine.BUS;
            } else transportType = TransportLine.TRAM;
            graph.removeTransportLine(transportLine);
            transportLine.setLineNumber(lineNumber);
            transportLine.setTransportType(transportType);
            editTransportLineStations(transportLine);
            graph.addTransportLine(transportLine);
            repaint();
        }
    }

    /**
     * zajmuje się edytowaniem przez użytkownika listy przystanków danej linii
     * @param transportLine
     */
    void editTransportLineStations(TransportLine transportLine) {
        transportLine.setStations(new ArrayList<>());
        ArrayList<Vertex> stations = graph.getStations();
        if (stations.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nie ma żadnych przystanków do wyboru!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean isVertexAdded = true;
        while (isVertexAdded) {
            Vertex newVertex = (Vertex) JOptionPane.showInputDialog(this, "Podaj następny przystanek i kliknij OK, kliknij Cancel gdy zostały dodane wszystkie przystanki\nObecne przystanki linii: " + transportLine.listOfStations(), "Lista przystanków linii", JOptionPane.PLAIN_MESSAGE, null, stations.toArray(), null);
            if (newVertex != null) {
                transportLine.addStation(newVertex);
            } else isVertexAdded = false;
        }
    }

    /**
     * użytkownik wybiera, którą linię podświetlić na czerwono, wtedy wszystkie inne linie będą szare
     */
    void highlightTransportLine() {
        TransportLine transportLine = (TransportLine) JOptionPane.showInputDialog(this, "Wybierz którą linię chcesz wyróżnić", "", JOptionPane.PLAIN_MESSAGE, null, graph.getTransportLines().toArray(), null);
        if (transportLine != null) {
            ArrayList<Edge> connections = graph.getConnections();
            for (Edge connection : connections) {
                if (transportLine.containsConnection(connection)) {
                    connection.setColor(Color.RED);
                } else {
                    connection.setColor(Color.GRAY);
                }
            }
            repaint();
            lineHighlighted = true;
        }
    }

    /**
     * ustawia wszystkim liniom domyślny kolor
     */
    void unhighlightTransportLine() {
        if (lineHighlighted) {
            ArrayList<TransportLine> transportLines = graph.getTransportLines();
            int transportLinesSize = transportLines.size();
            for (int i = 0; i < transportLinesSize; ++i) {
                //TODO na pewno można to optymalniej zrobić
                TransportLine transportLine = transportLines.get(0);
                graph.removeTransportLine(transportLine);
                graph.addTransportLine(transportLine);
            }
            repaint();
        }
    }

    /**
     * usunięcie wybranej przez użytkownika linii
     */
    void deleteTransportLine() {
        TransportLine transportLine = (TransportLine) JOptionPane.showInputDialog(this, "Wybierz którą linię chcesz usunąć", "", JOptionPane.PLAIN_MESSAGE, null, graph.getTransportLines().toArray(), null);
        graph.removeTransportLine(transportLine);
        repaint();
    }

    /**
     * tworzy nowe okno i wyświetla w nim wszystkie linie oraz liczbę ich przystanków
     */
    void showTransportLines() {
        ArrayList<TransportLine> transportLines = graph.getTransportLines();
        ArrayList<String> lineNumbers = new ArrayList<>();
        ArrayList<String> lineDestinations = new ArrayList<>();
        ArrayList<String> lineAmountOfStations = new ArrayList<>();
        for (TransportLine transportLine : transportLines) {
            lineNumbers.add(transportLine.getLineNumber());
            lineDestinations.add(transportLine.getDestination());
            lineAmountOfStations.add(String.valueOf(transportLine.getStations().size()));
        }
        String[] tableHeader = {"Numer linii", "Kierunek", "Ilość przystanków"};
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(lineNumbers);
        data.add(lineDestinations);
        data.add(lineAmountOfStations);
        new TableFrame("Wszystkie linie", tableHeader, data);
    }

    /**
     * zajmuje się wyborem odpowiedniej akcji w zależności od naciśniętego klawisza
     * @param e event naciśnięcia klawisza
     */
    void keyPressedHandling(KeyEvent e) {
        int key = e.getKeyCode();
        byte dist = 1;
        if (e.isShiftDown()) dist = 10;
        switch (key) {
            case KeyEvent.VK_LEFT:
                objectToMove().move(-dist, 0);
                break;
            case KeyEvent.VK_RIGHT:
                objectToMove().move(dist, 0);
                break;
            case KeyEvent.VK_UP:
                objectToMove().move(0, dist);
                break;
            case KeyEvent.VK_DOWN:
                objectToMove().move(0, -dist);
                break;
            case KeyEvent.VK_ADD:
                if (currentVertex != null) {
                    currentVertex.setR(currentVertex.getR() + dist);
                }
                break;
            case KeyEvent.VK_SUBTRACT:
                if (currentVertex != null) {
                    int currentR = currentVertex.getR() - 5; //-5, bo jest zaznaczony, a zaznaczenie powiększa o 5
                    if (currentR - dist > 0) {
                        currentVertex.setR(currentVertex.getR() - dist);
                    }
                }
                break;
            case KeyEvent.VK_DELETE:
                if (currentVertex != null) {
                    removeStation();
                }
                break;
        }
    }

    /**
     * zajmuje się wyborem odpowiedniej akcji w zależności od naciśniętego przycisku myszki
     * @param e
     */
    void mouseClickedHandling(MouseEvent e) {
        int pressedButton = e.getButton();
        mouseX = e.getX();
        mouseY = e.getY();
        if (pressedButton == MouseEvent.BUTTON1) {
            leftClick(mouseX, mouseY);
        } else if (pressedButton == MouseEvent.BUTTON3) {
            rightClick(mouseX, mouseY);
        }
    }

    /**
     * jeśli LPM jest trzymany to będzie można przesuwać przystanek/połączenie/graf
     * @param e
     */
    void mousePressedHandling(MouseEvent e) {
        int pressedButton = e.getButton();
        mouseX = e.getX();
        mouseY = e.getY();
        if (pressedButton == MouseEvent.BUTTON1) {
            leftClick(mouseX, mouseY);
            dragging = true;
        }
    }

    /**
     * jeśli LPM jest puszczony to nie można już przesuwać przystanku/połączenia/grafu
     * @param e
     */
    void mouseReleasedHandling(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            dragging = false;
    }

    /**
     * jeśli LPM jest trzymany, to przesuwa przystanek/połączenie/graf
     * @param e
     */
    void mouseDraggedHandling(MouseEvent e) {
        int newMouseX = e.getX();
        int newMouseY = e.getY();
        if (dragging) {
            objectToMove().move(newMouseX - mouseX, mouseY - newMouseY);
        }
        mouseX = newMouseX;
        mouseY = newMouseY;
    }

    /**
     * jeśli wykryto scroll myszki, odpowiednio dzieje się zoom in albo zoom out
     * @param e
     */
    void mouseWheelMovedHandling(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        ArrayList<Vertex> vertices = graph.getStations();
        boolean zoomIn = wheelRotation > 0;
        double zoomInOrOut;
        if (zoomIn) zoomInOrOut = 0.9;
        else zoomInOrOut = 1.1;
        for (Vertex vertex : vertices) {
            int newX=(int) (zoomInOrOut * vertex.getX());
            int newY=(int) (zoomInOrOut * vertex.getY());
            vertex.setX(newX);
            vertex.setY(newY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        graph.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        return;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressedHandling(e);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        return;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseClickedHandling(e);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressedHandling(e);
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseReleasedHandling(e);
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        return;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        return;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDraggedHandling(e);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        return;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelMovedHandling(e);
        repaint();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        if (graph != null) {
            this.graph = graph;
            repaint();
        }
    }

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }
}
