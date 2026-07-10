package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Dijalog sa odbrojavanjem u poslednjih 5s neaktivnosti.
// Ostaje sopstvena klasa (a ne JOptionPane) jer se tekst mora osvežavati svake sekunde.
public class WarningDialog extends JDialog {

    private final JLabel label;

    public WarningDialog(Frame owner, InactivityTimer timer) {
        super(owner, "Neaktivnost", false);   // treći argument false = NE-modalni (da ga tajmer osvežava)
        this.setLayout(new BorderLayout(10, 10));

        label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.BOLD, 28));
        label.setForeground(Color.RED);
        this.add(label, BorderLayout.CENTER);

        JButton cont = new JButton("Nastavi");
        cont.setFont(new Font("Verdana", Font.PLAIN, 24));
        cont.addActionListener(e -> timer.reset());   // reset ujedno gasi ovaj dijalog
        JPanel p = new JPanel(new FlowLayout());
        p.add(cont);
        this.add(p, BorderLayout.SOUTH);

        this.setSize(700, 200);
        this.setLocationRelativeTo(owner);

        // ovde NE koristimo setDefaultCloseOperation jer na zatvaranje "X"-om
        // hoćemo posebnu akciju (reset tajmera), a ne samo dispose
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.interrupt();   // zatvaranje "X"-om tumačimo kao "nastavi rad"
            }
        });

        this.setVisible(true);
    }

    // tajmer poziva ovo svake sekunde da osveži preostalo vreme
    public void setRemaining(int seconds) {
        label.setText("Zatvaranje za " + seconds + " s. Nastaviti rad?");
    }
}
