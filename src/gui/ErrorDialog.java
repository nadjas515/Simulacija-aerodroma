package gui;

import javax.swing.*;   // Swing components: JDialog, JLabel...
import java.awt.*;      // Font, Color (from AWT)

// Dialog for error messages
class ErrorDialog extends JDialog {

    // owner is a java.awt.Window so that both the main window and another dialog can be the parent
    ErrorDialog(java.awt.Window owner, String msg) {
        super(owner);
        this.setTitle("Error");
        // centered label
        JLabel title = new JLabel(msg, SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 30));
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        this.add(title);   // add() on a JDialog goes to its content pane

        this.setModal(true);                                     // blocks until closed
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // "X" closes and disposes the dialog
        this.setLocationRelativeTo(owner);                       // center over the parent
        this.pack();
        this.setVisible(true);
    }
}
