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
import javax.swing.table.DefaultTableModel; // table model
import java.awt.*;                          // layouts, Font, Toolkit, AWTEvent
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;


// Main application window
public class Window extends JFrame {
    private InactivityTimer timer;
    // table models are FIELDS so that refresh() can fill them from anywhere
    private final DefaultTableModel airportModel =
            new DefaultTableModel(new String[]{"Code", "Name", "X", "Y","Show"}, 0){
                public Class<?> getColumnClass(int col) {
                    return col == 4 ? Boolean.class : Object.class;   // column 4 = checkbox
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 4;   // only the checkbox is editable (everything else is read-only)
                }
            };
    private final DefaultTableModel flightModel =
            new DefaultTableModel(new String[]{"Start", "End", "Departure", "Duration"}, 0);

    private Simulation sim;

    private static final Font FONT = new Font("Verdana", Font.PLAIN, 40);

    Window() {
        super("Airport");
        this.setSize(3000, 1800);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MapPanel mapPanel = new MapPanel();

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(700, 0));   // wider right column so all table columns fit
                                                           // (height 0 is ignored, the window's CENTER determines it)

        // ---------- buttons (NORTH) ----------
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0, 1, 5, 5));  // 1 column, any number of rows, 5px gap

        JLabel timeLabel = makeLabel("Time: 00:00");
        sim=new Simulation(()->{
            timeLabel.setText("Time: " + TimeUtil.format(sim.getTime()));
            Window.this.repaint();
        },()->{
            new InfoDialog(Window.this,"Gotova simulacija");
        });

        buttons.add(timeLabel);

        JButton startButton = makeButton("Start",buttons);
        startButton.addActionListener(e -> {
            if (Flight.getFlights().isEmpty()) {
                new ErrorDialog(Window.this, "Nema letova za simulaciju");
                return;   // don't start the clock
            }
            Flight.reschedule();
            sim.startTimer();
            timer.pause();
        });

        JButton pauseButton = makeButton("Pause",buttons);
        pauseButton.addActionListener(e -> {
            sim.pauseTimer();
            timer.cont();
        });

        JButton stopButton = makeButton("Stop",buttons);
        stopButton.addActionListener(e -> {
            sim.stopTimer();
            timeLabel.setText("Time: 00:00");
            timer.cont();
            mapPanel.repaint();
        });


        JButton inputAirport = makeButton("Input Airport",buttons);
        inputAirport.addActionListener(e -> {
            new InputAirport(Window.this);   // modal dialog, blocks until closed
            refresh();                        // refresh the table afterwards (an airport may have been added)
            mapPanel.repaint();
        });

        JButton inputFlight = makeButton("Input flight",buttons);
        inputFlight.addActionListener(e -> {
            new InputFlight(Window.this);
            refresh();
            mapPanel.repaint();
        });


        JLabel input_file_label = makeLabel("Input file:");

        buttons.add(input_file_label);

        JTextField input_file = makeField(10);

        JButton browse = makeButton("Browse...",null);
        browse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            setFontRecursively(chooser, new Font("Verdana", Font.PLAIN, 30));
            chooser.setPreferredSize(new Dimension(800, 600));
            chooser.setCurrentDirectory(new File("./"));   // start in the working directory
            chooser.setFileFilter(new FileNameExtensionFilter("CSV i JSON", "csv", "json"));  // optional
            int result = chooser.showOpenDialog(Window.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                input_file.setText(file.getAbsolutePath());   // put the path into the existing text field
            }
        });

        JPanel browsePanel = new JPanel(new BorderLayout(5, 0));
        browsePanel.add(input_file, BorderLayout.CENTER);   // the field STRETCHES to fill the space
        browsePanel.add(browse, BorderLayout.EAST);         // the button keeps its width, on the right

        buttons.add(browsePanel);

        JButton load = makeButton("Load",buttons);
        load.addActionListener(e -> {
            String filename = input_file.getText();
            Reader reader = readerFor(extensionOf(input_file.getText()));
            if(reader==null){
                new ErrorDialog(Window.this, "Ne valja format fajla, prihvata se samo csv i json");
                return;
            }
            try {
                reader.read(filename);
                refresh();                                       // show the loaded data in the table
                new InfoDialog(Window.this, "Uspešno učitano");
            } catch (IOException ex) {
                new ErrorDialog(Window.this, "Fajl se ne može pročitati");
            } catch (FileNotExists | FormatException | AirportNotExists | AirportExists ex) {
                new ErrorDialog(Window.this, ex.getMessage());
            }
            mapPanel.repaint();
        });



        JLabel output_file_label = makeLabel("Output file:");

        buttons.add(output_file_label);

        JTextField output_file = makeField(10);

        buttons.add(output_file);

        JButton save = makeButton("Save",buttons);
        save.addActionListener(e -> {
            String filename = output_file.getText();
            Writer writer = writerFor(extensionOf(filename));
            if(writer==null){
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




        // clear all data (e.g. before loading a new file, to avoid duplicates)
        JButton clear = makeButton("Clear",buttons);
        clear.addActionListener(e -> {
            sim.stopTimer();
            timeLabel.setText("Time: 00:00");
            timer.cont();
            Flight.clearAll();
            Airport.clearAll();
            Airplane.clearAll();
            refresh();
            mapPanel.repaint();
        });


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

        // ---------- tables (CENTER) ----------
        JTable airportTable = new JTable(airportModel);
        int cols[]=new int[]{120,300,100,100,100};
        configureTable(airportTable,cols);


        JTable flightTable = new JTable(flightModel);
        cols= new int[]{100, 100, 150, 150};
        configureTable(flightTable,cols);

        // two tables in the "Airports" / "Flights" tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Verdana", Font.PLAIN, 30));
        tabs.addTab("Airports", new JScrollPane(airportTable));   // the table MUST be inside a JScrollPane
        tabs.addTab("Flights",  new JScrollPane(flightTable));
        sidebar.add(tabs, BorderLayout.CENTER);

        this.add(sidebar, BorderLayout.EAST);

        // mapPanel goes DIRECTLY into CENTER so BorderLayout stretches it over the whole area
        this.add(mapPanel, BorderLayout.CENTER);

        this.setVisible(true);

        // inactivity timer (independent of the GUI components)
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

    // shared table styling (font, row height, header font)
    private void configureTable(JTable table,int cols[]) {
        table.setRowHeight(50);
        table.setFont(new Font("Verdana", Font.PLAIN, 25));
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 30));
        for(int i=0;i<cols.length;i++){
            table.getColumnModel().getColumn(i).setPreferredWidth(cols[i]);
        }
    }

    // fills both tables from the current data; called after every input and Load
    private void refresh() {
        airportModel.setRowCount(0);// remove old rows
        for (Airport a : Airport.getAirports())
            airportModel.addRow(a.toRow());

        flightModel.setRowCount(0);
        Flight.reschedule();
        for (Flight f : Flight.getFlights())
            flightModel.addRow(f.toRow());
    }

    private JButton makeButton(String label,JPanel panel) {
        JButton button = new JButton(label);
        button.setFont(FONT);
        if(panel!=null) panel.add(button);
        return button;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT);
        return l;
    }
    private JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FONT);
        return f;
    }

    private String extensionOf(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private Reader readerFor(String ext) {
        if (ext.equals("json")) return new JSONReader();
        if (ext.equals("csv"))  return new CSVReader();
        return null;   // the caller shows the error
    }

    private Writer writerFor(String ext) {
        if (ext.equals("json")) return new JSONWriter();
        if (ext.equals("csv"))  return new CSVWriter();
        return null;   // the caller shows the error
    }


    public static void main(String[] args) {
        // Swing rule: the GUI is created on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new Window());
    }
}
