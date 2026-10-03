package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Countdown dialog shown during the last 5 s of inactivity.
// A custom class (not JOptionPane) because the text has to be updated every second.
public class WarningDialog extends JDialog {

    private final JLabel label;

    public WarningDialog(Frame owner, InactivityTimer timer) {
        super(owner, "Neaktivnost", false);   // third argument false = NON-modal (so the timer can update it)
        this.setLayout(new BorderLayout(10, 10));

        label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.BOLD, 28));
        label.setForeground(Color.RED);
        this.add(label, BorderLayout.CENTER);

        JButton cont = new JButton("Nastavi");
        cont.setFont(new Font("Verdana", Font.PLAIN, 24));
        cont.addActionListener(e -> timer.reset());   // reset also closes this dialog
        JPanel p = new JPanel(new FlowLayout());
        p.add(cont);
        this.add(p, BorderLayout.SOUTH);

        this.setSize(700, 200);
        this.setLocationRelativeTo(owner);

        // setDefaultCloseOperation is NOT used here, because closing with "X"
        // should reset the timer, not just dispose the dialog
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.interrupt();   // closing with "X" means "continue working"
            }
        });

        this.setVisible(true);
    }

    // called by the timer every second to update the remaining time
    public void setRemaining(int seconds) {
        label.setText("Zatvaranje za " + seconds + " s. Nastaviti rad?");
    }
}
