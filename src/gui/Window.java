package gui;

import data.*;
import data.reader.*;
import data.writer.*;
import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;
import logic.Simulation;
import util.TimeUtil;

import javax.swing.*;                       // JFrame, JButton, JLabel, JTextField, JPanel, JTable, JTabbedPane
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel; // model tabele
import java.awt.*;                          // layout-i, Font, Toolkit, AWTEvent
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;


// U Swing-u glavni prozor je JFrame (umesto AWT Frame)
class Window extends JFrame {
    private InactivityTimer timer;
    // modeli tabela su POLJA klase - da bi refresh() mogao da ih puni sa bilo kog mesta
    private final DefaultTableModel airportModel =
            new DefaultTableModel(new String[]{"Code", "Name", "X", "Y","Show"}, 0){
                public Class<?> getColumnClass(int col) {
                    return col == 4 ? Boolean.class : Object.class;   // kolona 4 = checkbox
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 4;   // samo checkbox sme da se menja (ostalo read-only)
                }
            };
    private final DefaultTableModel flightModel =
            new DefaultTableModel(new String[]{"Start", "End", "Departure", "Duration"}, 0);

    private Simulation sim;

    Window() {
        super("Airport");
        this.setSize(3000, 1800);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MapPanel mapPanel = new MapPanel();

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(700, 0));   // šira desna kolona da stanu sve kolone tabele
                                                           // (visina 0 nije bitna - CENTER prozora je diktira)

        // ---------- dugmad (NORTH) ----------
        JButton inputAirport = new JButton("Input airport");
        inputAirport.setFont(new Font("Verdana", Font.PLAIN, 40));
        inputAirport.addActionListener(e -> {
            new InputAirport(Window.this);   // modalni dijalog - blokira dok se ne zatvori
            refresh();                        // po zatvaranju osveži tabelu (možda je dodat aerodrom)
            mapPanel.repaint();
        });

        JButton inputFlight = new JButton("Input flight");
        inputFlight.setFont(new Font("Verdana", Font.PLAIN, 40));
        inputFlight.addActionListener(e -> {
            new InputFlight(Window.this);
            refresh();
            mapPanel.repaint();
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
            mapPanel.repaint();
        });

        JButton browse = new JButton("Browse...");
        browse.setFont(new Font("Verdana", Font.PLAIN, 40));
        browse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            setFontRecursively(chooser, new Font("Verdana", Font.PLAIN, 30));
            chooser.setPreferredSize(new Dimension(800, 600));
            chooser.setCurrentDirectory(new File("files"));   // startuj u folderu "files" (opciono)
            chooser.setFileFilter(new FileNameExtensionFilter("CSV i JSON", "csv", "json"));  // opciono
            int result = chooser.showOpenDialog(Window.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                input_file.setText(file.getAbsolutePath());   // upiši putanju u postojeće polje
            }
        });

        JPanel browsePanel = new JPanel(new BorderLayout(5, 0));
        browsePanel.add(input_file, BorderLayout.CENTER);   // polje se RASTEGNE na sav prostor
        browsePanel.add(browse, BorderLayout.EAST);         // dugme ostane svoje širine, desno

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


        JLabel timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(new Font("Verdana", Font.PLAIN, 40));
        sim=new Simulation(()->{
            timeLabel.setText("Time: " + TimeUtil.format(sim.getTime()));
            Window.this.repaint();
        });

        // isprazni sve podatke (npr. pre učitavanja novog fajla, da nema duplikata)
        JButton clear = new JButton("Clear");
        clear.setFont(new Font("Verdana", Font.PLAIN, 40));
        clear.addActionListener(e -> {
            sim.stopTimer();
            timeLabel.setText("Time: 00:00");
            timer.cont();
            Flight.clearAll();
            Airplane.clearAll();
            refresh();
            mapPanel.repaint();
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0, 1, 5, 5));  // 1 kolona, koliko god redova, razmak 5px


        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Verdana", Font.PLAIN, 40));
        startButton.addActionListener(e -> {
            if (Flight.getFlights().isEmpty()) {
                new ErrorDialog(Window.this, "Nema letova za simulaciju");
                return;   // ne pokreći sat
            }
            Flight.reschedule();
            sim.startTimer();
            timer.pause();
        });

        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Verdana", Font.PLAIN, 40));
        pauseButton.addActionListener(e -> {
            sim.pauseTimer();
            timer.cont();
        });

        JButton stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Verdana", Font.PLAIN, 40));
        stopButton.addActionListener(e -> {
            sim.stopTimer();
            timeLabel.setText("Time: 00:00");

            timer.cont();
            mapPanel.repaint();
        });

        buttons.add(timeLabel);
        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stopButton);
        buttons.add(inputAirport);
        buttons.add(inputFlight);
        buttons.add(input_file_label);
        buttons.add(browsePanel);
        buttons.add(load);
        buttons.add(output_file_label);
        buttons.add(output_file);
        buttons.add(save);
        buttons.add(clear);
        sidebar.add(buttons, BorderLayout.NORTH);


        airportModel.addTableModelListener(e->{
            if(e.getColumn()==4){
                int row=e.getFirstRow();
                boolean shown = (Boolean) airportModel.getValueAt(row,4);
                String code = (String) airportModel.getValueAt(row,0);
                try {
                    Airport.findAirport(code).setVisible(shown);
                } catch (AirportNotExists ex) {}
                mapPanel.repaint();
            }
        });

        // ---------- tabele (CENTER) ----------
        JTable airportTable = new JTable(airportModel);
        configureTable(airportTable);
        // širine kolona za tabelu aerodroma
        airportTable.getColumnModel().getColumn(0).setPreferredWidth(120);   // Code
        airportTable.getColumnModel().getColumn(1).setPreferredWidth(300);   // Name
        airportTable.getColumnModel().getColumn(2).setPreferredWidth(100);   // X
        airportTable.getColumnModel().getColumn(3).setPreferredWidth(100);   // Y
        airportTable.getColumnModel().getColumn(4).setPreferredWidth(100);   // Y

        JTable flightTable = new JTable(flightModel);
        configureTable(flightTable);

        flightTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        flightTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        flightTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        flightTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // dve tabele u tabovima "Airports" / "Flights"
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Verdana", Font.PLAIN, 30));
        tabs.addTab("Airports", new JScrollPane(airportTable));   // tabela MORA u JScrollPane
        tabs.addTab("Flights",  new JScrollPane(flightTable));
        sidebar.add(tabs, BorderLayout.CENTER);

        this.add(sidebar, BorderLayout.EAST);

        // mapPanel ide DIREKTNO u CENTER da ga BorderLayout rastegne na ceo prostor
        this.add(mapPanel, BorderLayout.CENTER);

        this.setVisible(true);

        // tajmer neaktivnosti - radi isto kao pre (ne zavisi od AWT/Swing komponenti)
        timer = new InactivityTimer(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            int id = event.getID();
            if (id == MouseEvent.MOUSE_PRESSED || id == KeyEvent.KEY_PRESSED)
                timer.reset();
        }, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
        timer.start();

        mapPanel.setTimer(timer);
    }

    private static void setFontRecursively(Component c, Font font) {
        c.setFont(font);
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                setFontRecursively(child, font);
            }
        }
    }

    // zajedničko podešavanje izgleda tabele (font, visina reda, font zaglavlja)
    private void configureTable(JTable table) {
        table.setRowHeight(50);
        table.setFont(new Font("Verdana", Font.PLAIN, 25));
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 30));
    }

    // puni obe tabele iz trenutnih podataka - zove se posle svakog unosa i Load-a
    private void refresh() {
        airportModel.setRowCount(0);// obriši stare redove
        for (Airport a : Airport.getAirports())
            airportModel.addRow(a.toRow());

        flightModel.setRowCount(0);
        Flight.reschedule();
        for (Flight f : Flight.getFlights())
            flightModel.addRow(f.toRow());
    }



    public static void main(String[] args) {
        // Swing preporuka: GUI se pravi na Event Dispatch niti (EDT)
        SwingUtilities.invokeLater(() -> new Window());
    }
}
