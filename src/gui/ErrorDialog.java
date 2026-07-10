package gui;

import javax.swing.*;   // Swing komponente: JDialog, JLabel...
import java.awt.*;      // Font, Color (ostaju iz AWT-a)

// U Swing-u dijalog je JDialog (umesto AWT Dialog)
class ErrorDialog extends JDialog {

    // owner je java.awt.Window da bi prihvatio i glavni prozor i drugi dijalog kao roditelja
    ErrorDialog(java.awt.Window owner, String msg) {
        super(owner);
        this.setTitle("Error");
        // JLabel: poravnanje se zadaje preko SwingConstants (umesto starog Label.CENTER)
        JLabel title = new JLabel(msg, SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 30));
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        this.add(title);   // add() na JDialog automatski ide na "content pane"

        this.setModal(true);                                     // blokira dok se ne zatvori
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // "X" zatvara i oslobađa dijalog
        this.setLocationRelativeTo(owner);                       // centriraj preko roditelja
        this.pack();
        this.setVisible(true);
    }
}
