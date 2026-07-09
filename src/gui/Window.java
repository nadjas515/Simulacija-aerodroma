package gui;

import data.*;
import data.reader.*;
import data.writer.*;
import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

import javax.swing.*;                       // JFrame, JButton, JLabel, JTextField, JPanel, JTable, JTabbedPane
import javax.swing.table.DefaultTableModel; // model tabele
import java.awt.*;                          // layout-i, Font, Toolkit, AWTEvent
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;


// U Swing-u glavni prozor je JFrame (umesto AWT Frame)
class Window extends JFrame {

    // modeli tabela su POLJA klase - da bi refresh() mogao da ih puni sa bilo kog mesta
    private final DefaultTableModel airportModel =
            new DefaultTableModel(new String[]{"Code", "Name", "X", "Y"}, 0);
    private final DefaultTableModel flightModel =
            new DefaultTableModel(new String[]{"Start", "End", "Departure", "Duration"}, 0);

    Window() {
        super("Airport");
        this.setSize(1500, 800);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---------- dugmad (NORTH) ----------
        JButton inputAirport = new JButton("Input airport");
        inputAirport.setFont(new Font("Verdana", Font.PLAIN, 40));
        inputAirport.addActionListener(e -> {
            new InputAirport(Window.this);   // modalni dijalog - blokira dok se ne zatvori
            refresh();                        // po zatvaranju osveži tabelu (možda je dodat aerodrom)
        });

        JButton inputFlight = new JButton("Input flight");
        inputFlight.setFont(new Font("Verdana", Font.PLAIN, 40));
        inputFlight.addActionListener(e -> {
            new InputFlight(Window.this);
            refresh();
        });

        JLabel input_file_label = new JLabel("Input file:");
        input_file_label.setFont(new Font("Verdana", Font.PLAIN, 40));

        JTextField input_file = new JTextField(10);
        input_file.setFont(new Font("Verdana", Font.PLAIN, 40));

        JButton load = new JButton("Load");
        load.setFont(new Font("Verdana", Font.PLAIN, 40));
        load.addActionListener(e -> {
            String filename = input_file.getText();
            String ext = filename.substring(filename.lastIndexOf('.') + 1);
            Reader reader;
            if (ext.equals("json")) {
                reader = new JSONReader();
            } else if (ext.equals("csv")) {
                reader = new CSVReader();
            } else {
                new ErrorDialog(Window.this, "Ne valja format fajla, prihvata se samo csv i json");
                return;
            }
            try {
                reader.read(filename);
                refresh();                                       // prikaži učitane podatke u tabeli
                new InfoDialog(Window.this, "Uspešno učitano");
            } catch (IOException ex) {
                new ErrorDialog(Window.this, "Fajl se ne može pročitati");
            } catch (FileNotExists | FormatException | AirportNotExists | AirportExists ex) {
                new ErrorDialog(Window.this, ex.getMessage());
            }
        });

        JLabel output_file_label = new JLabel("Output file:");
        output_file_label.setFont(new Font("Verdana", Font.PLAIN, 40));

        JTextField output_file = new JTextField(10);
        output_file.setFont(new Font("Verdana", Font.PLAIN, 40));

        JButton save = new JButton("Save");
        save.setFont(new Font("Verdana", Font.PLAIN, 40));
        save.addActionListener(e -> {
            String filename = output_file.getText();
            String ext = filename.substring(filename.lastIndexOf('.') + 1);
            Writer writer;
            if (ext.equals("json")) {
                writer = new JSONWriter();
            } else if (ext.equals("csv")) {
                writer = new CSVWriter();
            } else {
                new ErrorDialog(Window.this, "Ne valja format fajla, prihvata se samo csv i json");
                return;
            }
            try {
                writer.write(filename);
                new InfoDialog(Window.this, "Uspešno sačuvano");
            } catch (IOException ex) {
                new ErrorDialog(Window.this, "Fajl se ne može sačuvati");
            }
        });

        // isprazni sve podatke (npr. pre učitavanja novog fajla, da nema duplikata)
        JButton clear = new JButton("Clear");
        clear.setFont(new Font("Verdana", Font.PLAIN, 40));
        clear.addActionListener(e -> {
            Airport.clearAll();
            Flight.clearAll();
            refresh();
        });

        JPanel p = new JPanel(new FlowLayout());
        p.add(inputAirport);
        p.add(inputFlight);
        p.add(input_file_label);
        p.add(input_file);
        p.add(load);
        p.add(output_file_label);
        p.add(output_file);
        p.add(save);
        p.add(clear);
        this.add(p, BorderLayout.NORTH);

        // ---------- tabele (CENTER) ----------
        JTable airportTable = new JTable(airportModel);
        configureTable(airportTable);
        // širine kolona za tabelu aerodroma
        airportTable.getColumnModel().getColumn(0).setPreferredWidth(120);   // Code
        airportTable.getColumnModel().getColumn(1).setPreferredWidth(500);   // Name
        airportTable.getColumnModel().getColumn(2).setPreferredWidth(100);   // X
        airportTable.getColumnModel().getColumn(3).setPreferredWidth(100);   // Y

        JTable flightTable = new JTable(flightModel);
        configureTable(flightTable);

        // dve tabele u tabovima "Airports" / "Flights"
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Verdana", Font.PLAIN, 24));
        tabs.addTab("Airports", new JScrollPane(airportTable));   // tabela MORA u JScrollPane
        tabs.addTab("Flights",  new JScrollPane(flightTable));
        this.add(tabs, BorderLayout.CENTER);

        this.setVisible(true);

        // tajmer neaktivnosti - radi isto kao pre (ne zavisi od AWT/Swing komponenti)
        InactivityTimer timer = new InactivityTimer(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            int id = event.getID();
            if (id == MouseEvent.MOUSE_PRESSED || id == KeyEvent.KEY_PRESSED)
                timer.reset();
        }, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        timer.start();
    }

    // zajedničko podešavanje izgleda tabele (font, visina reda, font zaglavlja)
    private void configureTable(JTable table) {
        table.setRowHeight(50);
        table.setFont(new Font("Verdana", Font.PLAIN, 30));
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 30));
    }

    // puni obe tabele iz trenutnih podataka - zove se posle svakog unosa i Load-a
    void refresh() {
        airportModel.setRowCount(0);                 // obriši stare redove
        for (Airport a : Airport.getAirports())
            airportModel.addRow(a.toRow());

        flightModel.setRowCount(0);
        for (Flight f : Flight.getFlights())
            flightModel.addRow(f.toRow());
    }

    public static void main(String[] args) {
        // Swing preporuka: GUI se pravi na Event Dispatch niti (EDT)
        SwingUtilities.invokeLater(() -> new Window());
    }
}
