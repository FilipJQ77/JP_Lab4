/*
    Program: Aplikacja z możliwością działań na sieci komunikacji miejskiej
    Plik: GraphApp.java
    Autor: Filip Przygoński, 248892
    Data: Grudzień 2019
*/

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * TODO DOKUMENTACJA
 */
public class GraphApp extends JFrame implements ActionListener {
    //TODO spróbować zrobić znalezienie trasy z X do Y
    public static final String TITLE = "Mapa komunikacji miejskiej";
    public static final String AUTHOR = "Autor: Filip Przygoński, 248892, Grudzień 2019";
    public static final String HELP = "";//TODO INSTRUKCJA

    GraphPanel graphPanel;

    JMenuItem menuNewGraph = new JMenuItem("Nowa sieć");
    JMenuItem menuExample = new JMenuItem("Przykład sieci");
    JMenuItem menuLoad = new JMenuItem("Wczytaj sieć");
    JMenuItem menuSave = new JMenuItem("Zapisz sieć");
    JMenuItem menuShowStations = new JMenuItem("Pokaż listę przystanków");
    JMenuItem menuShowTransportLineStations = new JMenuItem("Pokaż przystanki wybranej linii");
    JMenuItem menuNewTransportLine = new JMenuItem("Stwórz nową linię");
    JMenuItem menuEditTransportLine = new JMenuItem("Edytuj wybraną linię");
    JMenuItem menuHighlightTransportLine = new JMenuItem("Wyróżnij wybraną linię");
    JMenuItem menuDeleteTransportLine = new JMenuItem("Usuń wybraną linię");
    JMenuItem menuShowTransportLines = new JMenuItem("Pokaż listę linii");
    JMenuItem menuHelp = new JMenuItem("Pomoc");
    JMenuItem menuAuthor = new JMenuItem("Autor");

    public GraphApp() {
        super(TITLE);
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menuNetwork = new JMenu("Sieć komunikacji");
        JMenu menuStation = new JMenu("Przystanki");
        JMenu menuTransportLine = new JMenu("Linie komunikacji");
        JMenu menuInfo = new JMenu("Pomoc");

        menuBar.add(menuNetwork);
        menuBar.add(menuStation);
        menuBar.add(menuTransportLine);
        menuBar.add(menuInfo);

        menuNetwork.add(menuNewGraph);
        menuNetwork.add(menuExample);
        menuNetwork.add(menuLoad);
        menuNetwork.add(menuSave);
        menuStation.add(menuShowStations);
        menuStation.add(menuShowTransportLineStations);
        menuTransportLine.add(menuNewTransportLine);
        menuTransportLine.add(menuEditTransportLine);
        menuTransportLine.add(menuHighlightTransportLine);
        menuTransportLine.add(menuDeleteTransportLine);
        menuTransportLine.add(menuShowTransportLines);
        menuInfo.add(menuHelp);
        menuInfo.add(menuAuthor);

        menuNewGraph.addActionListener(this);
        menuExample.addActionListener(this);
        menuLoad.addActionListener(this);
        menuSave.addActionListener(this);
        menuShowStations.addActionListener(this);
        menuNewTransportLine.addActionListener(this);
        menuEditTransportLine.addActionListener(this);
        menuShowTransportLineStations.addActionListener(this);
        menuHighlightTransportLine.addActionListener(this);
        menuDeleteTransportLine.addActionListener(this);
        menuShowTransportLines.addActionListener(this);
        menuHelp.addActionListener(this);
        menuAuthor.addActionListener(this);

        graphPanel = new GraphPanel();
        setContentPane(graphPanel);
        setVisible(true);
    }

    private File chooseFile() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }

    private void loadGraphFromFile(File file) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Graph graph = (Graph) inputStream.readObject();
            graphPanel.setGraph(graph);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas odczytu", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        graphPanel.unhighlightTransportLine();
        Object sourceOfEvent = e.getSource();
        if (sourceOfEvent == menuNewGraph) {
            graphPanel.setGraph(new Graph());
        } else if (sourceOfEvent == menuExample) {
            File file = new File("example.bin");//TODO może lepiej zrób to w kodzie
            loadGraphFromFile(file);
        } else if (sourceOfEvent == menuLoad) {
            File file = chooseFile();
            loadGraphFromFile(file);
        } else if (sourceOfEvent == menuSave) {
            File file = chooseFile();
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(graphPanel.getGraph());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas zapisu", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (sourceOfEvent == menuShowStations) {
            graphPanel.showStations();
        } else if (sourceOfEvent == menuShowTransportLineStations) {
            graphPanel.showTransportLineStations();
        }  else if (sourceOfEvent == menuNewTransportLine) {
            graphPanel.createTransportLine();
        } else if (sourceOfEvent == menuEditTransportLine) {
            graphPanel.editTransportLine();
        } else if (sourceOfEvent == menuHighlightTransportLine) {
            graphPanel.highlightTransportLine();
        } else if (sourceOfEvent == menuDeleteTransportLine) {
            graphPanel.deleteTransportLine();
        } else if (sourceOfEvent == menuShowTransportLines) {
            graphPanel.showTransportLines();
        } else if (sourceOfEvent == menuHelp) {
            JOptionPane.showMessageDialog(this, HELP, "Pomoc", JOptionPane.INFORMATION_MESSAGE);
        } else if (sourceOfEvent == menuAuthor) {
            JOptionPane.showMessageDialog(this, AUTHOR, "Autor", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new GraphApp();
    }
}