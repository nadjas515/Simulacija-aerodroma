package gui;

import data.Airport;
import exceptions.AirportExists;
import exceptions.FormatException;

import javax.swing.*;   // JDialog, JPanel, JLabel, JTextField, JButton, BorderFactory, SwingConstants
import java.awt.*;      // layout-i (BorderLayout, GridLayout, FlowLayout), Font

// U Swing-u dijalog je JDialog (umesto AWT Dialog)
public class InputAirport extends JDialog {

    public InputAirport(Frame owner) {
        super(owner);
        this.setLayout(new BorderLayout(5, 5));
        this.setModal(true);                                     // blokira glavni prozor dok je otvoren
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // "X" zatvara dijalog

        // Margina oko celog sadržaja - Swing zamena za stari override getInsets().
        // getContentPane() je podrazumevano JPanel, pa mu možemo dati "prazan" okvir (padding).
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JPanel p = new JPanel(new GridLayout(0, 2, 50, 10));  // 2 kolone: labela + polje

        // JLabel: poravnanje desno se zadaje kroz SwingConstants.RIGHT (umesto Label.RIGHT)
        JLabel name_label = new JLabel("Name:", SwingConstants.RIGHT);
        name_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(name_label);
        JTextField name = new JTextField(20);
        name.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(name);

        JLabel code_label = new JLabel("Code:", SwingConstants.RIGHT);
        code_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(code_label);
        JTextField code = new JTextField(20);
        code.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(code);

        JLabel x_label = new JLabel("X coordinate:", SwingConstants.RIGHT);
        x_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(x_label);
        JTextField x = new JTextField(20);
        x.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(x);

        JLabel y_label = new JLabel("Y coordinate:", SwingConstants.RIGHT);
        y_label.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(y_label);
        JTextField y = new JTextField(20);
        y.setFont(new Font("Verdana", Font.PLAIN, 40));
        p.add(y);

        JPanel button_panel = new JPanel(new FlowLayout());
        JButton ok = new JButton("OK");
        ok.setFont(new Font("Verdana", Font.PLAIN, 40));

        // logika validacije je ista kao pre - Swing menja samo komponente, ne i logiku
        ok.addActionListener(e -> {
            String nameVal = name.getText();
            String codeVal = code.getText();
            String xVal    = x.getText();
            String yVal    = y.getText();

            try {
                if (nameVal.isEmpty() || codeVal.isEmpty() || xVal.isEmpty() || yVal.isEmpty()) {
                    throw new FormatException("Sva polja trebaju biti popunjena");
                }
                boolean codeOk = codeVal.length() == 3;
                for (char c : codeVal.toCharArray())
                    if (!Character.isUpperCase(c))   // isUpperCase je true SAMO za velika slova (ne cifre)
                        codeOk = false;
                if (!codeOk) {
                    throw new FormatException("Kod treba da bude tačno 3 velika slova");
                }
                int xi = Integer.parseInt(xVal.trim());
                int yi = Integer.parseInt(yVal.trim());
                if(Math.abs(xi)>180)
                    throw new FormatException("X koordinata mora biti između -180 i 180");
                if(Math.abs(yi)>90)
                    throw new FormatException("Y koordinata mora biti između -90 i 90");

                Airport.addAirport(xi, yi, nameVal, codeVal);
                Airport.printAirports();
                dispose();
            }
            catch (NumberFormatException ex) {
                new ErrorDialog(InputAirport.this,"Brojevi nisu u dobrom formatu");
            }
            catch (AirportExists | FormatException ex) {
                new ErrorDialog(InputAirport.this,ex.getMessage());
            }
        });

        button_panel.add(ok);

        this.add(p, BorderLayout.CENTER);             // labele + polja (dve kolone)
        this.add(button_panel, BorderLayout.SOUTH);   // OK preko cele širine
        this.pack();                                  // veličina prema sadržaju
        this.setLocationRelativeTo(owner);            // centriraj preko glavnog prozora
        this.setVisible(true);
    }
}
