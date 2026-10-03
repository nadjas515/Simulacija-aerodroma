package gui;

import data.Airport;
import exceptions.AirportExists;
import exceptions.FormatException;

import javax.swing.*;   // JDialog, JPanel, JLabel, JTextField, JButton, BorderFactory, SwingConstants
import java.awt.*;      // layouts (BorderLayout, GridLayout, FlowLayout), Font

// Dialog for adding a new airport
public class InputAirport extends JDialog {

    public InputAirport(Frame owner) {
        super(owner);
        this.setLayout(new BorderLayout(5, 5));
        this.setModal(true);                                     // blocks the main window while open
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // "X" closes the dialog

        // Padding around the whole content.
        // getContentPane() is a JPanel by default, so it can be given an empty border (padding).
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JPanel p = new JPanel(new GridLayout(0, 2, 50, 10));  // 2 columns: label + field

        // right-aligned labels
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

        // input validation
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
                    if (!Character.isUpperCase(c))   // isUpperCase is true ONLY for uppercase letters (not digits)
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

        this.add(p, BorderLayout.CENTER);             // labels + fields (two columns)
        this.add(button_panel, BorderLayout.SOUTH);   // full-width OK button
        this.pack();                                  // size to fit the content
        this.setLocationRelativeTo(owner);            // center over the main window
        this.setVisible(true);
    }
}
