package gui;

import javax.swing.*;
import java.awt.*;

// Dialog for information/success messages (green text)
public class InfoDialog extends JDialog {

    InfoDialog(java.awt.Window owner, String msg) {
        super(owner);

        JLabel title = new JLabel(msg, SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 30));
        title.setForeground(new Color(0, 140, 0));   // green = information/success
        this.add(title);

        this.setSize(800, 200);
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }
}
