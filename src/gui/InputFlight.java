package gui;

import data.Airport;
import data.Flight;
import exceptions.AirportNotExists;
import exceptions.FormatException;

import javax.swing.*;
import java.awt.*;

// U Swing-u dijalog je JDialog (umesto AWT Dialog)
public class InputFlight extends JDialog {

    public InputFlight(Frame owner) {
        super(owner);
        this.setLayout(new BorderLayout(5, 5));
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // margina oko sadržaja (Swing zamena za getInsets)
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JPanel p = new JPanel(new GridLayout(0, 2, 50, 10));  // 2 kolone: labela + polje

        JLabel from_label = new JLabel("From:", SwingConstants.RIGHT);
        from_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(from_label);
        JTextField from = new JTextField(20);
        from.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(from);

        JLabel to_label = new JLabel("To:", SwingConstants.RIGHT);
        to_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(to_label);
        JTextField to = new JTextField(20);
        to.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(to);

        JLabel time_label = new JLabel("Time:", SwingConstants.RIGHT);
        time_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(time_label);

        // mali panel: sat : minut  (u jednoj ćeliji desne kolone)
        JPanel time_panel = new JPanel(new FlowLayout());
        JTextField hour = new JTextField(5);
        hour.setFont(new Font("Verdana", Font.PLAIN, 40));
        time_panel.add(hour);
        JLabel delim = new JLabel(":");
        delim.setFont(new Font("Verdana", Font.PLAIN, 40));
        time_panel.add(delim);
        JTextField min = new JTextField(5);
        min.setFont(new Font("Verdana", Font.PLAIN, 40));
        time_panel.add(min);
        p.add(time_panel);

        JLabel duration_label = new JLabel("Duration:", SwingConstants.RIGHT);
        duration_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(duration_label);
        JTextField duration = new JTextField(20);
        duration.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(duration);

        JPanel button_panel = new JPanel(new FlowLayout());
        JButton ok = new JButton("OK");
        ok.setFont(new Font("Verdana", Font.PLAIN, 40));

        // validacija ista kao pre
        ok.addActionListener(e -> {
            String fromVal     = from.getText();
            String toVal       = to.getText();
            String hourVal     = hour.getText();
            String minVal      = min.getText();
            String durationVal = duration.getText();

            try {
                if (fromVal.isEmpty() || toVal.isEmpty() || hourVal.isEmpty()
                        || minVal.isEmpty() || durationVal.isEmpty()) {
                    throw new FormatException("Sva polja trebaju biti popunjena");
                }
                int h   = Integer.parseInt(hourVal.trim());
                int m   = Integer.parseInt(minVal.trim());
                int dur = Integer.parseInt(durationVal.trim());
                if (h < 0 || h > 23)
                    throw new FormatException("Sati moraju biti između 0 i 23");
                if (m < 0 || m > 59)
                    throw new FormatException("Minuti moraju biti između 0 i 59");
                if (dur <= 0)
                    throw new FormatException("Trajanje mora biti pozitivan broj");

                Airport start = Airport.findAirport(fromVal);
                Airport end   = Airport.findAirport(toVal);
                Flight.addFlights(start, end, h, m, dur);
                Flight.printFlights();
                dispose();
            }
            catch (NumberFormatException ex) {
                new ErrorDialog(InputFlight.this, "Brojevi nisu u dobrom formatu");
            }
            catch (AirportNotExists | FormatException ex) {
                new ErrorDialog(InputFlight.this, ex.getMessage());
            }
        });

        button_panel.add(ok);

        this.add(p, BorderLayout.CENTER);
        this.add(button_panel, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }
}
